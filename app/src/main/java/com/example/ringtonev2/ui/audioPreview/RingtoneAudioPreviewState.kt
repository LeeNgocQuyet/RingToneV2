package com.example.ringtonev2.ui.audioPreview

import com.example.ringtonev2.domain.RingtoneAudioPreview

sealed class RingtoneAudioPreviewState {
    object Idle : RingtoneAudioPreviewState()
    object Loading : RingtoneAudioPreviewState()

    data class Success(
        val data: RingtoneAudioPreview
    ) : RingtoneAudioPreviewState()

    data class Error(val message: Any) : RingtoneAudioPreviewState()
}