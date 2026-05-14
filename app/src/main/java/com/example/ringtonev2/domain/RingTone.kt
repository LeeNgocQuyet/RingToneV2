package com.example.ringtonev2.domain

import com.google.gson.annotations.SerializedName

data class Ringtone(
    val id: Int,
    @SerializedName("category_id")
    val categoryId: Int?,
    val name: String?,
    @SerializedName("watch_count")
    val watchCount: Int?,
    @SerializedName("audio_path")
    val audioPath: String?,
    val image: String?,
    val duration: Long
)
