package com.example.ringtonev2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ringtonev2.data.local.entity.RingtoneEntity

@Dao
interface RingtoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ringtone: RingtoneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RingtoneEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRingtone(ringtone: RingtoneEntity)

    @Query("""UPDATE ringtones SET filePath = :path WHERE id = :id""")
    suspend fun updateFilePath(id: String, path: String)

    @Query("SELECT * FROM ringtones WHERE id = :id LIMIT 1")
    suspend fun getRingtoneById(id: String): RingtoneEntity?

}