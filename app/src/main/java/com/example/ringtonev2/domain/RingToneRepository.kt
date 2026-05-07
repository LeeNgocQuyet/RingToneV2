package com.example.ringtonev2.domain;

import androidx.paging.PagingData

import java.util.List;

import kotlinx.coroutines.flow.Flow

interface RingtoneRepository {

    /** Paged trending feed. */
    fun trendingPager(pageSize: Int = 20): Flow<PagingData<Ringtone>>

    /** Paged feed filtered by [category]. */
    fun categoryPager(category: String, pageSize: Int = 20): Flow<PagingData<Ringtone>>

    /** Paged search results. Emits empty when [query] is blank. */
    fun searchPager(query: String, pageSize: Int = 20): Flow<PagingData<Ringtone>>

    suspend fun getTrending(): List<Ringtone>

    suspend fun getCategories(): List<String>

    suspend fun getByCategory(category: String): List<Ringtone>

    suspend fun search(query: String): List<Ringtone>

    suspend fun getById(id: String): Ringtone?

    //fun observeFavorites(): Flow<List<FavoriteItem>>

    suspend fun isFavorite(ringtoneId: String): Boolean

    suspend fun toggleFavorite(ringtone: Ringtone)

    fun observeDownloads(): Flow<List<DownloadItem>>

    suspend fun downloadRingtone(ringtone: Ringtone): Result<DownloadItem>

    suspend fun deleteDownload(id: Long)
}