package com.example.ringtonev2.data.repository

import androidx.paging.ExperimentalPagingApi
import com.example.ringtonev2.data.local.AppDatabase
import com.example.ringtonev2.data.local.dao.DownloadDao
import com.example.ringtonev2.data.local.dao.FavoriteDao
import com.example.ringtonev2.data.local.dao.RingtoneDao
import com.example.ringtonev2.data.local.entity.DownloadedRingtone
import com.example.ringtonev2.data.local.entity.RingtoneEntity
import com.example.ringtonev2.domain.DownloadItem
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneRepository
import com.example.ringtonev2.data.mapper.toDomain
import com.example.ringtonev2.data.mapper.toFavoriteEntity
import com.example.ringtonev2.data.mapper.toRingtoneEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RingtoneRepositoryImpl @Inject constructor(
    private val downloadDao: DownloadDao,
    private val favoriteDao: FavoriteDao,
    private val ringtoneDao: RingtoneDao

) : RingtoneRepository {

    override suspend fun deleteDownload(id: Long) {
        downloadDao.delete(id)
    }
    override fun observeDownloads(): Flow<List<Ringtone>> {
        return ringtoneDao.observeAll()
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

    override fun observeFavorites(): Flow<List<Ringtone>> {
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

    override suspend fun getRingtoneById(id: String): RingtoneEntity? {
        return ringtoneDao.getRingtoneById(id)
    }

    override suspend fun updateFilePath(ringtoneId: String, filePath: String) {
        ringtoneDao.updateFilePath(ringtoneId, filePath)
    }

    override suspend fun downloadRingtone(ringtone: Ringtone) {
        ringtoneDao.insertRingtone(ringtone.toRingtoneEntity())
    }
}