package com.example.ringtonev2.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.ringtonev2.data.local.AppDatabase
import com.example.ringtonev2.data.local.dao.DownloadDao
import com.example.ringtonev2.data.local.entity.DownloadedRingtone
import com.example.ringtonev2.domain.DownloadItem
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneRepository
import com.example.ringtonev2.data.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RingtoneRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val downloadDao: DownloadDao,

) : RingtoneRepository {


    private fun pagingConfig(pageSize: Int) = PagingConfig(
        pageSize = pageSize,
        initialLoadSize = pageSize,
        prefetchDistance = pageSize / 2,
        enablePlaceholders = false,
    )

    override suspend fun deleteDownload(id: Long) {
        downloadDao.delete(id)
    }
    override fun observeDownloads(): Flow<List<DownloadItem>> {
        return downloadDao.observeAll()
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun downloadRingtone(ringtone: Ringtone): Result<DownloadItem> {
        TODO("Not yet implemented")
    }

    override fun trendingPager(pageSize: Int): Flow<PagingData<Ringtone>> {
        TODO("Not yet implemented")
    }

    override fun categoryPager(
        category: String,
        pageSize: Int
    ): Flow<PagingData<Ringtone>> {
        TODO("Not yet implemented")
    }

    override fun searchPager(
        query: String,
        pageSize: Int
    ): Flow<PagingData<Ringtone>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTrending(): java.util.List<Ringtone> {
        TODO("Not yet implemented")
    }

    override suspend fun getCategories(): java.util.List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getByCategory(category: String): java.util.List<Ringtone> {
        TODO("Not yet implemented")
    }

    override suspend fun search(query: String): java.util.List<Ringtone> {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: String): DownloadedRingtone? {
        return downloadDao.getByRingtoneId(id)
    }

    override suspend fun getByRingtoneId(ringtoneId: String): DownloadedRingtone? {
        return downloadDao.getByRingtoneId(ringtoneId)
    }

    override suspend fun isFavorite(ringtoneId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun toggleFavorite(ringtone: Ringtone) {
        TODO("Not yet implemented")
    }
}