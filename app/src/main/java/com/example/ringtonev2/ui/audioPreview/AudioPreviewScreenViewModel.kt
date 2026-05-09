package com.example.ringtonev2.ui.audioPreview

import android.content.ContentValues
import android.content.Context
import android.media.RingtoneManager
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.data.remote.api.ApiService
import com.example.ringtonev2.domain.RingtoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

data class AudioPreviewUiState(
    val title: String = "",
    val audioPath: String = "",
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false
)

enum class RingtoneType(val value: Int) {
    RINGTONE(RingtoneManager.TYPE_RINGTONE),
    NOTIFICATION(RingtoneManager.TYPE_NOTIFICATION),
    ALARM(RingtoneManager.TYPE_ALARM)
}

@HiltViewModel
class AudioPreviewScreenViewModel @Inject constructor(
    private val repository: RingtoneRepository,
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AudioPreviewUiState())
    val uiState = _uiState.asStateFlow()

    fun load(audioId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val localAudio = repository.getByRingtoneId(audioId)

            if (localAudio != null) {
                _uiState.value = _uiState.value.copy(
                    title = localAudio.title ?: "",
                    duration = localAudio.duration ?: 0L,
                    audioPath = localAudio.filePath ?: "",
                    isLoading = false
                )
            } else {
                try {
                    val response = api.getRingtones(listIds = audioId)
                    if (response.status && response.data.isNotEmpty()) {
                        val remoteAudio = response.data.first()
                        _uiState.value = _uiState.value.copy(
                            title = remoteAudio.name ?: "",
                            duration = (remoteAudio.duration?.toLong() ?: 0L) ,
                            audioPath = remoteAudio.audioPath ?: "",
                            isLoading = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun togglePlay() {
        _uiState.value = _uiState.value.copy(
            isPlaying = !_uiState.value.isPlaying
        )
    }

    fun seekTo(position: Long) {
        _uiState.value = _uiState.value.copy(
            currentPosition = position
        )
    }

    fun setPlaying(isPlaying: Boolean) {
        _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
    }

    fun setAsSystemSound(context: Context, type: RingtoneType, onSuccess: () -> Unit) {
        val appContext = context.applicationContext
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val path = _uiState.value.audioPath
                if (path.isEmpty() || path.startsWith("http")) return@launch
                
                val internalFile = File(path)
                if (!internalFile.exists()) return@launch

                val resolver = appContext.contentResolver
                val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, internalFile.name)
                    put(MediaStore.MediaColumns.TITLE, _uiState.value.title)
                    put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
                    val folder = when (type) {
                        RingtoneType.ALARM -> Environment.DIRECTORY_ALARMS
                        RingtoneType.NOTIFICATION -> Environment.DIRECTORY_NOTIFICATIONS
                        else -> Environment.DIRECTORY_RINGTONES
                    }
                    put(MediaStore.MediaColumns.RELATIVE_PATH, folder)
                    put(MediaStore.Audio.Media.IS_RINGTONE, type == RingtoneType.RINGTONE)
                    put(MediaStore.Audio.Media.IS_NOTIFICATION, type == RingtoneType.NOTIFICATION)
                    put(MediaStore.Audio.Media.IS_ALARM, type == RingtoneType.ALARM)
                }

                val newUri = resolver.insert(audioUri, values)
                if (newUri != null) {
                    resolver.openOutputStream(newUri)?.use { output ->
                        FileInputStream(internalFile).use { input -> input.copyTo(output) }
                    }
                    RingtoneManager.setActualDefaultRingtoneUri(appContext, type.value, newUri)
                    withContext(Dispatchers.Main) { onSuccess() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
