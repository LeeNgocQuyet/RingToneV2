package com.example.ringtonev2.domain

import com.google.gson.annotations.SerializedName

data class Ringtone(
    val id: Int,
    val category_id: Int?,
    val name: String?,
    val watch_count: Int?,
    val audio_path: String?,
    val image: String?,
    val duration: Int?
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
