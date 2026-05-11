package com.example.ringtonev2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ringtonev2.domain.Category
import com.google.gson.annotations.SerializedName
import kotlin.time.Duration

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val ringtoneId: String,
    val duration: Int,
    val title: String,
    val audioUrl: String,
    val categoryId: Int )