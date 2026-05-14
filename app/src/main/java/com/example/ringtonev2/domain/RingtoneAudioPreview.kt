package com.example.ringtonev2.domain

data class RingtoneAudioPreview(
    val ringtoneId: String,
    val title: String,
    val audioPath: String,
    val duration: Long,
    val currentPosition: Long = 0L,
    val isPlaying: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Int = 0,
    val isDownloaded: Boolean = false
)