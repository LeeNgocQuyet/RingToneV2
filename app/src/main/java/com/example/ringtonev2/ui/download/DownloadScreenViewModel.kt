package com.example.ringtonev2.ui.download

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ringtonev2.data.repository.RetrofitInstance
import com.example.ringtonev2.ui.download.AudioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadScreenViewModel @Inject constructor() : ViewModel() {
    private val _audioState = MutableStateFlow<AudioState>(AudioState.Idle)
    val audioState: StateFlow<AudioState> = _audioState

    fun resetAudioState() {
        _audioState.value = AudioState.Idle
    }

    fun getInfoAudio(context: Context, link: String) {
        _audioState.value = AudioState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAudio(link)
                val infoAudio = response?.data?.data
                if (infoAudio != null) {
                    _audioState.value = AudioState.Success(infoAudio)
                } else {
                    _audioState.value = AudioState.Error("Data null")
                }

            } catch (e: Exception) {
                _audioState.value = AudioState.Error(e.message ?: "Unknown error")
            }
        }
    }
}