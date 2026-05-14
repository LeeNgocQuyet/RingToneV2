package com.example.ringtonev2.domain;

import com.example.ringtonev2.data.local.entity.DownloadedRingtone
import com.example.ringtonev2.data.local.entity.RingtoneEntity

import kotlinx.coroutines.flow.Flow

interface RingtoneRepository {
    suspend fun getById(id: String): DownloadedRingtone?
    suspend fun getByRingtoneId(ringtoneId: String): DownloadedRingtone?
    fun observeDownloads(): Flow<kotlin.collections.List<DownloadItem>>
    suspend fun deleteDownload(id: Long)

    fun observeFavorites(): Flow<kotlin.collections.List<Ringtone>>

    fun isFavorite(
        ringtoneId: String
    ): Flow<Boolean>

    suspend fun addFavorite(
        ringtone: Ringtone
    )

    suspend fun removeFavorite(
        ringtoneId: String
    )

    suspend fun toggleFavorite(
        ringtone: Ringtone
    )
    suspend fun downloadRingtone(
        ringtone: Ringtone
    )
    suspend fun updateFilePath(
        ringtoneId: String, filePath: String
    )
    suspend fun getRingtoneById(
        id: String): RingtoneEntity?
}