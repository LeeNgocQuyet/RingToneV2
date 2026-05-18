package com.example.ringtonev2.domain;

import com.example.ringtonev2.data.local.entity.TikTokDownloadEntity
import com.example.ringtonev2.data.local.entity.DownloadedRingtoneEntity

import kotlinx.coroutines.flow.Flow

interface RingtoneRepository {
    suspend fun getTikTokDownloadByRingtoneId(ringtoneId: String): TikTokDownloadEntity?
    suspend fun deleteTikTokDownloadByLocalId(id: Long)
    fun observeDownloadedRingtones(): Flow<kotlin.collections.List<Ringtone>>

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
    suspend fun saveDownloadedRingtone(
        ringtoneEntity: DownloadedRingtoneEntity
    )
    suspend fun updateDownloadedRingtoneFilePath(
        ringtoneId: String, filePath: String
    )
    suspend fun getDownloadedRingtoneById(
        id: String): DownloadedRingtoneEntity?

    suspend fun deleteDownloadedRingtoneById(
        ringtoneId: String)
}
