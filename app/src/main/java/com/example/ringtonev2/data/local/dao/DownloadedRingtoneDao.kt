package com.example.ringtonev2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ringtonev2.data.local.entity.DownloadedRingtoneEntity

@Dao
interface DownloadedRingtoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDownloadedRingtone(ringtone: DownloadedRingtoneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDownloadedRingtones(items: List<DownloadedRingtoneEntity>)

    // ToDo Tại sao lại dùng """ AI Code à
    @Query("UPDATE ringtones SET filePath = :path WHERE id = :id")
    suspend fun updateDownloadedRingtoneFilePath(id: String, path: String)

    @Query("SELECT * FROM ringtones WHERE id = :id LIMIT 1")
    suspend fun getDownloadedRingtoneById(id: String): DownloadedRingtoneEntity?

    @Query("SELECT * FROM ringtones ORDER BY cachedAt DESC")
    fun observeDownloadedRingtones(): kotlinx.coroutines.flow.Flow<List<DownloadedRingtoneEntity>>

    @Query("DELETE FROM ringtones WHERE id = :ringtoneId")
    suspend fun deleteDownloadedRingtoneById(ringtoneId: String)
}
