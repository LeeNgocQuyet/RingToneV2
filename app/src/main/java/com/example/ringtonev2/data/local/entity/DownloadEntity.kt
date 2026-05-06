package com.example.ringtonev2.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "downloads",
    indices = [Index(value = ["ringtoneId"], unique = true)])
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val ringtoneId: String,
    val title: String,
    val artist: String,
    val filePath: String,
    val downloadedAt: Long,
)