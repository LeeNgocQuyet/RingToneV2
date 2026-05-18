package com.example.ringtonev2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ringtonev2.data.local.entity.TikTokDownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TikTokDownloadDao {

    @Query("SELECT * FROM downloads ORDER BY downloadedAt DESC")
    fun observeTikTokDownloads(): Flow<List<TikTokDownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTikTokDownload(download: TikTokDownloadEntity): Long

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteTikTokDownloadByLocalId(id: Long)

    @Query("SELECT * FROM downloads WHERE ringtoneId = :ringtoneId")
    suspend fun getTikTokDownloadByRingtoneId(ringtoneId: String): TikTokDownloadEntity?
}
