package com.example.ringtonev2.ui.audioInfo

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.data.local.dao.DownloadDao
import com.example.ringtonev2.data.local.entity.DownloadEntity
import com.example.ringtonev2.data.remote.dto.TikTokData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

sealed interface AudioDownloadUiState {
    data object Idle : AudioDownloadUiState
    data class Downloading(val progress: Float) : AudioDownloadUiState
    data class Success(val savedPath: String) : AudioDownloadUiState
    data object Error : AudioDownloadUiState
}

@HiltViewModel
class AudioDownloadViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val downloadDao: DownloadDao

) : ViewModel() {

    private val client = OkHttpClient()

    private val _uiState = MutableStateFlow<AudioDownloadUiState>(AudioDownloadUiState.Idle)
    val uiState: StateFlow<AudioDownloadUiState> = _uiState.asStateFlow()

    fun markInvalidUrl() {
        _uiState.value = AudioDownloadUiState.Error
    }

    fun startDownloadIfNeeded(audioUrl: String,data: TikTokData) {
        if (_uiState.value != AudioDownloadUiState.Idle) return
        downloadFileInternal(audioUrl,data)
    }

    fun downloadFileInternal(audioUrl: String,data: TikTokData) {
        val fileName = "tiktok_${System.currentTimeMillis()}.mp3"
        val file = File(appContext.filesDir, fileName)
        viewModelScope.launch {
            _uiState.value = AudioDownloadUiState.Downloading(0f)
            withContext(Dispatchers.IO) {
                try {
                    val request = Request.Builder()
                        .url(audioUrl)
                        .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10)")
                        .build()
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        Log.e("downloadFileInternal", "Server returned ${response.code}")
                        _uiState.value = AudioDownloadUiState.Error
                        return@withContext
                    }
                    val body = response.body ?: run {
                        _uiState.value = AudioDownloadUiState.Error
                        return@withContext
                    }
                    val contentLength = body.contentLength()
                    body.byteStream().use { input ->
                        FileOutputStream(file).use { output ->
                            val data = ByteArray(4096)
                            var totalRead = 0L
                            var lastEmittedPct = -1
                            while (true) {
                                val count = input.read(data)
                                if (count == -1) break
                                output.write(data, 0, count)
                                totalRead += count
                                if (contentLength > 0) {
                                    val pct = ((totalRead * 100) / contentLength).toInt().coerceIn(0, 100)
                                    if (pct != lastEmittedPct) {
                                        lastEmittedPct = pct
                                        val p = (totalRead.toFloat() / contentLength).coerceIn(0f, 1f)
                                        _uiState.value = AudioDownloadUiState.Downloading(p)
                                    }
                                }
                            }
                        }
                    }
                    Log.d("AudioDownloadViewModel", "Saved internal: ${file.absolutePath}")
                    val entity = DownloadEntity(
                        ringtoneId = data.id ?: "",
                        title = data.title ?: "Unknown",
                        artist = data.author?.nickname ?: "Unknown",
                        filePath = file.absolutePath,
                        downloadedAt = System.currentTimeMillis(),
                        duration = data.duration ?: 0L
                    )

                    downloadDao.insert(entity)
                    Log.d("AudioDownloadViewModel", "Saved to database: $entity")
                    _uiState.value = AudioDownloadUiState.Success(file.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = AudioDownloadUiState.Error
                }
            }
        }
    }
}
