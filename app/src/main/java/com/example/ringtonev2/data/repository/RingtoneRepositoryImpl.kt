package com.example.ringtonev2.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.ringtonev2.data.local.AppDatabase
import com.example.ringtonev2.data.local.dao.DownloadDao
import com.example.ringtonev2.data.local.dao.FavoriteDao
import com.example.ringtonev2.data.local.entity.DownloadedRingtone
import com.example.ringtonev2.data.local.entity.FavoriteEntity
import com.example.ringtonev2.domain.DownloadItem
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneRepository
import com.example.ringtonev2.data.mapper.toDomain
import com.example.ringtonev2.data.mapper.toFavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RingtoneRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val downloadDao: DownloadDao,
    private val favoriteDao: FavoriteDao

) : RingtoneRepository {

    override suspend fun deleteDownload(id: Long) {
        downloadDao.delete(id)
    }
    override fun observeDownloads(): Flow<List<DownloadItem>> {
        return downloadDao.observeAll()
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun getById(id: String): DownloadedRingtone? {
        return downloadDao.getByRingtoneId(id)
    }

    override suspend fun getByRingtoneId(ringtoneId: String): DownloadedRingtone? {
        return downloadDao.getByRingtoneId(ringtoneId)
    }

    override fun getFavorites(): Flow<List<Ringtone>> {
        return favoriteDao.getFavorites()
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override fun isFavorite(
        ringtoneId: String
    ): Flow<Boolean> {
        return favoriteDao.isFavorite(ringtoneId)
    }

    override suspend fun addFavorite(
        ringtone: Ringtone
    ) {
        favoriteDao.insertFavorite(
            ringtone.toFavoriteEntity()
        )
    }

    override suspend fun removeFavorite(
        ringtoneId: String
    ) {
        favoriteDao.deleteFavoriteById(ringtoneId)
    }

    override suspend fun toggleFavorite(
        ringtone: Ringtone
    ) {
        val ringtoneId = ringtone.id.toString()

        val existing =
            favoriteDao.getFavoriteById(
                ringtoneId
            )

        if (existing == null) {
            favoriteDao.insertFavorite(
                ringtone.toFavoriteEntity()
            )
        } else {
            favoriteDao.deleteFavoriteById(
                ringtoneId
            )
        }
    }
}