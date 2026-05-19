package com.example.ringtonev2.data.repository

import androidx.paging.ExperimentalPagingApi
import com.example.ringtonev2.data.local.dao.FavoriteDao
import com.example.ringtonev2.data.local.dao.DownloadedRingtoneDao
import com.example.ringtonev2.data.local.entity.DownloadedRingtoneEntity
import com.example.ringtonev2.domain.Ringtone
import com.example.ringtonev2.domain.RingtoneRepository
import com.example.ringtonev2.data.mapper.toDomain
import com.example.ringtonev2.data.mapper.toFavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RingtoneRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val downloadedRingtoneDao: DownloadedRingtoneDao

) : RingtoneRepository {

    override fun observeDownloadedRingtones(): Flow<List<Ringtone>> {
        return downloadedRingtoneDao.observeDownloadedRingtones()
            .map { list ->
                list.map { it.toDomain() }
            }
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

    override suspend fun getDownloadedRingtoneById(id: String): DownloadedRingtoneEntity? {
        return downloadedRingtoneDao.getDownloadedRingtoneById(id)
    }

    override suspend fun updateDownloadedRingtoneFilePath(ringtoneId: String, filePath: String) {
        downloadedRingtoneDao.updateDownloadedRingtoneFilePath(ringtoneId, filePath)
    }

    override suspend fun saveDownloadedRingtone(ringtoneEntity: DownloadedRingtoneEntity) {
        downloadedRingtoneDao.saveDownloadedRingtone(ringtoneEntity)
    }
    override suspend fun deleteDownloadedRingtoneById(ringtoneId: String) {
        downloadedRingtoneDao.deleteDownloadedRingtoneById(ringtoneId)
    }
}
