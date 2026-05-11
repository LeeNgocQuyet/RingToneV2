package com.example.ringtonev2.domain;

import androidx.paging.PagingData
import com.example.ringtonev2.data.local.entity.DownloadedRingtone

import java.util.List;

import kotlinx.coroutines.flow.Flow

interface RingtoneRepository {
    suspend fun getById(id: String): DownloadedRingtone?
    suspend fun getByRingtoneId(ringtoneId: String): DownloadedRingtone?
    fun observeDownloads(): Flow<kotlin.collections.List<DownloadItem>>
    suspend fun deleteDownload(id: Long)

    fun getFavorites(): Flow<kotlin.collections.List<Ringtone>>

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
}