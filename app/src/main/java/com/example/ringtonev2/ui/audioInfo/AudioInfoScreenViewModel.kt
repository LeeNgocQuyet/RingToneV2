package com.example.ringtonev2.ui.audioInfo

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioInfoScreenViewModel @Inject constructor() : ViewModel() {

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    fun togglePlay(currentPlaying: Boolean, onPlay: () -> Unit, onPause: () -> Unit) {
        if (currentPlaying) {
            onPause()
            _isPlaying.value = false
        } else {
            onPlay()
            _isPlaying.value = true
        }
    }

    fun setPlaying(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }
}