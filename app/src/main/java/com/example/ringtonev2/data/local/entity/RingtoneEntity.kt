package com.example.ringtonev2.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "ringtones",
    primaryKeys = ["id"],
)
data class RingtoneEntity(
    val id: String,
    val position: Int,
    val title: String,
    val artist: String,
    val category: String,
    val duration: Long,
    val coverUrl: String,
    val audioUrl: String,
    val plays: Int,
    val cachedAt: Long,
    val filePath: String?,
)