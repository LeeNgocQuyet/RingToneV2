package com.example.ringtonev2.domain

import com.google.gson.annotations.SerializedName

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

data class Category(
    val id: Int,
    val icon: String,
    val name: String,
    val weight: Int,
    @SerializedName("background_image")
    val backgroundImage: String,
    val description: String
)
