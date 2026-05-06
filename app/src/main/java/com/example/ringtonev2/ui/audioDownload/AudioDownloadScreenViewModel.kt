package com.example.ringtonev2.ui.audioDownload

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import androidx.compose.runtime.State

data class DownloadState(
    val progress: Float = 0f,
    val isDone: Boolean = false
)

@HiltViewModel
class AudioDownloadScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = mutableStateOf(DownloadState())
    val state: State<DownloadState> = _state
    fun downloadFileInternal(audioUrl: String) {
        val fileName = "tiktok_${System.currentTimeMillis()}.mp3"
        val file = File(context.filesDir, fileName)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(audioUrl)
            .addHeader("User-Agent", "Mozilla/5.0")
            .build()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        Log.e("downloadFileInternal", "Server returned ${response.code}")
                        return@withContext
                    }
                    val body = response.body ?: return@withContext
                    Log.d("OKHTTP", "Code: ${response.code}")
                    Log.d("OKHTTP", "Message: ${response.message}")
                    var downloadedBytes = 0L
                    val totalBytes = body.contentLength()
                    val input = body.byteStream()
                    val output = FileOutputStream(file)
                    val data = ByteArray(4096)
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        output.write(data, 0, count)

                        downloadedBytes += count

                        val progress = if (totalBytes > 0)
                            downloadedBytes.toFloat() / totalBytes
                        else 0f

                        _state.value = _state.value.copy(progress = progress)
                    }

                    output.flush()
                    output.close()
                    input.close()
                    _state.value = _state.value.copy(
                        progress = 1f,
                        isDone = true
                    )

                    Log.d("DOWNLOAD", "Saved internal: ${file.absolutePath}")

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}