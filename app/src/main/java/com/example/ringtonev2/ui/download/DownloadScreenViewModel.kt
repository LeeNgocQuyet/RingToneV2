package com.example.ringtonev2.ui.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.R
import com.example.ringtonev2.data.repository.RetrofitInstance
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadScreenViewModel @Inject constructor(
    private val repository: RingtoneRepository
) : ViewModel() {
    private val _audioState = MutableStateFlow<AudioState>(AudioState.Idle)
    val audioState: StateFlow<AudioState> = _audioState

    val downloadHistory =
        repository.observeDownloadedRingtones()
            .catch {
                emit(emptyList())
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val favoriteIds =
        repository.observeFavorites()
            .map { list -> list.map { it.id }.toSet() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet()
            )

    private val _currentPlayingId = MutableStateFlow<String?>(null)
    val currentPlayingId = _currentPlayingId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    fun resetAudioState() {
        _audioState.value = AudioState.Idle
    }

    fun getInfoAudio(link: String) {
        _audioState.value = AudioState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAudio(link)
                val infoAudio = response?.data?.data
                if (infoAudio != null) {
                    _audioState.value = AudioState.Success(infoAudio)
                } else {
                    _audioState.value = AudioState.Error(R.string.data_null)
                }

            } catch (e: Exception) {
                _audioState.value = AudioState.Error(e.message ?: R.string.unknown_error)
            }
        }
    }

    fun toggleFavorite(ringtone: Ringtone) {
        viewModelScope.launch {
            repository.toggleFavorite(ringtone)
        }
    }

    fun togglePlaying(ringtoneId: String) {
        if (_currentPlayingId.value == ringtoneId) {
            _isPlaying.value = !_isPlaying.value
        } else {
            _currentPlayingId.value = ringtoneId
            _isPlaying.value = true
        }
    }

    fun onPlaybackCompleted() {
        _isPlaying.value = false
    }
}
