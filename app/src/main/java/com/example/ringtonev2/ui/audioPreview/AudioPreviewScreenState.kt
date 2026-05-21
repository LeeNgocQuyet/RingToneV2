package com.example.ringtonev2.ui.audioPreview

data class AudioPreviewUiState(
    val title: String = "",
    val audioPath: String = "",
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false
)