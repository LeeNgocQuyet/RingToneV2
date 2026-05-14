package com.example.ringtonev2.ui.audioPreview

import android.content.ContentValues
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.data.local.entity.RingtoneEntity
import com.example.ringtonev2.data.mapper.toAudioPreview
import com.example.ringtonev2.data.mapper.toDomain
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
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject


@HiltViewModel
class RingtoneAudioPreviewScreenViewModel @Inject constructor(
    private val repository: RingtoneRepository,
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<RingtoneAudioPreviewState>(RingtoneAudioPreviewState.Idle)
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

    fun resetState() {
        _uiState.value = RingtoneAudioPreviewState.Idle
    }
    fun load(audioId: String) {
        viewModelScope.launch {
            _uiState.value = RingtoneAudioPreviewState.Loading

            val localAudio = repository.getRingtoneById(audioId)

            if (localAudio != null && !localAudio.filePath.isNullOrEmpty()) {
                _uiState.value = RingtoneAudioPreviewState.Success(
                    data = localAudio.toDomain().toAudioPreview(
                        isDownloaded = true,
                        audioPathOverride = Uri.fromFile(File(localAudio.filePath)).toString()
                    )
                )
            } else {
                try {
                    val response = api.getRingtones(listIds = audioId)
                    if (response.status && response.data.isNotEmpty()) {
                        val remoteAudio = response.data.first()
                        if (remoteAudio.audioPath.isNullOrBlank()) {
                            _uiState.value = RingtoneAudioPreviewState.Error("Audio path not found")
                            return@launch
                        }
                        _uiState.value = RingtoneAudioPreviewState.Success(
                            data = remoteAudio.toAudioPreview(
                                isDownloaded = false
                            )
                        )
                    } else {
                        _uiState.value = RingtoneAudioPreviewState.Error("Data not found")
                    }
                } catch (e: Exception) {
                    _uiState.value = RingtoneAudioPreviewState.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }

    fun toggleFavorite(ringtoneId: String) {
        viewModelScope.launch {
            val ringtoneEntity = repository.getRingtoneById(ringtoneId)

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

    fun seekTo(position: Long) {
        val currentState = _uiState.value
        if (currentState is RingtoneAudioPreviewState.Success) {
            _uiState.value = currentState.copy(data = currentState.data.copy(currentPosition = position))
        }
    }

    fun setPlaying(isPlaying: Boolean) {
        val currentState = _uiState.value
        if (currentState is RingtoneAudioPreviewState.Success) {
            _uiState.value = currentState.copy(data = currentState.data.copy(isPlaying = isPlaying))
        }
    }

    fun downloadRingtone(context: Context) {
        val currentState = _uiState.value
        if (currentState !is RingtoneAudioPreviewState.Success) return

        val previewData = currentState.data
        if (previewData.ringtoneId.isBlank()) {
            _uiState.value = RingtoneAudioPreviewState.Error("Invalid ringtone id")
            return
        }
        val url = previewData.audioPath
        if (url.isBlank() || !url.startsWith("http")) {
            _uiState.value = RingtoneAudioPreviewState.Error("Invalid audio url")
            return
        }


        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = currentState.copy(data = previewData.copy(isDownloading = true, downloadProgress = 0))

                val fileName = "ringtone_${previewData.ringtoneId}.mp3"
                val file = File(context.filesDir, fileName)

                val connection = URL(url).openConnection()
                connection.connect()
                val fileLength = connection.contentLength
                val input = BufferedInputStream(connection.getInputStream())
                val output = FileOutputStream(file)

                val data = ByteArray(1024)
                var total: Long = 0
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    total += count
                    val progress = if (fileLength > 0) (total * 100 / fileLength).toInt() else 0

                    // Cập nhật %
                    val updatedState = _uiState.value
                    if (updatedState is RingtoneAudioPreviewState.Success) {
                        _uiState.value = updatedState.copy(data = updatedState.data.copy(downloadProgress = progress))
                    }
                    output.write(data, 0, count)
                }

                output.close()
                input.close()

                val ringtoneDomain = RingtoneEntity(
                    position = 0,
                    id = previewData.ringtoneId,
                    title = previewData.title,
                    artist = "",
                    category = "",
                    duration = previewData.duration,
                    coverUrl = "",
                    audioUrl = Uri.fromFile(File(file.absolutePath)).toString(),
                    plays = 0,
                    cachedAt = System.currentTimeMillis(),
                    filePath = file.absolutePath
                    )

                repository.downloadRingtoneEntity(ringtoneDomain)

                repository.updateFilePath(
                    previewData.ringtoneId,
                    file.absolutePath
                )

                withContext(Dispatchers.Main) {
                    val finalState = _uiState.value
                    if (finalState is RingtoneAudioPreviewState.Success) {
                        _uiState.value = finalState.copy(data = finalState.data.copy(
                            isDownloading = false,
                            isDownloaded = true,
                            audioPath = file.absolutePath)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val errorState = _uiState.value
                if (errorState is RingtoneAudioPreviewState.Success) {
                    _uiState.value = errorState.copy(data = errorState.data.copy(isDownloading = false, downloadProgress = 0))
                }
            }
        }
    }

    fun setAsSystemSound(context: Context, type: RingtoneType, onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState !is RingtoneAudioPreviewState.Success) return
        val appContext = context.applicationContext
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val path = currentState.data.audioPath
                if (path.isEmpty() || path.startsWith("http")) return@launch

                val internalFile = File(path)
                if (!internalFile.exists()) return@launch

                val resolver = appContext.contentResolver
                val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, internalFile.name)
                    put(MediaStore.MediaColumns.TITLE, currentState.data.title)
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
