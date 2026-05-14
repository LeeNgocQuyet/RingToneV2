package com.example.ringtonev2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val ringtoneId: String,
    val duration: Long,
    val title: String,
    val audioUrl: String,
    val categoryId: Int
)