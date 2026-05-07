package com.example.ringtonev2.ui.audioPreview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.domain.RingtoneRepository
import com.example.ringtonev2.ui.audioInfo.AudioDownloadUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AudioPreviewUiState(
    val title: String = "",
    val audioPath: String = "",
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val isPlaying: Boolean = false
)

@HiltViewModel
class AudioPreviewScreenViewModel @Inject constructor(
    private val repository: RingtoneRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AudioPreviewUiState())
    val uiState = _uiState.asStateFlow()

    fun load(audioId: String) {
        viewModelScope.launch {

            val audio = repository.getByRingtoneId(audioId)

            _uiState.value = _uiState.value.copy(
                title = audio?.title ?: "",
                duration = audio?.duration ?: 0L,
                audioPath = audio?.filePath ?: ""
            )
            Log.d("AudioPreviewScreenViewModel", "load: ${audio?.filePath}")
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
        _uiState.value =
            _uiState.value.copy(
                isPlaying = isPlaying
            )
    }
}
