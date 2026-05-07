package com.example.ringtonev2.domain

data class Ringtone(
    val id: String,
    val title: String,
    val artist: String,
    val category: String,
    val durationSec: Int,
    val coverUrl: String,
    val audioUrl: String,
    val plays: Int,
)

data class DownloadItem(
    val id: Long,
    val ringtoneId: String,
    val title: String,
    val artist: String,
    val filePath: String,
    val downloadedAt: Long,
    val duration: Long
)
