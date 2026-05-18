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
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val favoriteIds =
        repository.observeFavorites()
            .map { list ->
                list.map { it.id }.toSet()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet()
            )

    fun load(audioId: String) {
        viewModelScope.launch {

            val audio = repository.getTikTokDownloadByRingtoneId(audioId)

            _uiState.value = _uiState.value.copy(
                title = audio?.title ?: "",
                duration = audio?.duration ?: 0L,
                audioPath = audio?.filePath ?: ""
            )
        }
    }

    fun toggleFavorite(ringtoneId: String) {
        viewModelScope.launch {
            val ringtoneEntity = repository.getDownloadedRingtoneById(ringtoneId)

            if (ringtoneEntity == null) {
                return@launch
            }

            val ringtone = Ringtone(
                id = ringtoneEntity.id,
                categoryId = ringtoneEntity.category.toIntOrNull(),
                name = ringtoneEntity.title,
                duration = ringtoneEntity.duration,
                audioPath = ringtoneEntity.audioUrl,
                watchCount = ringtoneEntity.plays,
                image = ringtoneEntity.coverUrl,
            )

            repository.toggleFavorite(ringtone)
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
