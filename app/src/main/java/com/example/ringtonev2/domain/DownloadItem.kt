package com.example.ringtonev2.domain

data class DownloadItem(
    val id: Long,
    val ringtoneId: String,
    val title: String,
    val artist: String,
    val filePath: String,
    val downloadedAt: Long,
    val duration: Long
)