package com.example.ringtonev2.ui.audioPreview

sealed class RingtoneAudioPreviewState {
    object Idle : RingtoneAudioPreviewState()
    object Loading : RingtoneAudioPreviewState()

    data class Success(
        val ringtoneId: String,
        val title: String,
        val audioPath: String,
        val duration: Long,
        val currentPosition: Long = 0L,
        val isPlaying: Boolean = false,
        val isDownloading: Boolean = false,
        val downloadProgress: Int = 0,
        val isDownloaded: Boolean = false
    ) : RingtoneAudioPreviewState()

    data class Error(val message: String) : RingtoneAudioPreviewState()
}