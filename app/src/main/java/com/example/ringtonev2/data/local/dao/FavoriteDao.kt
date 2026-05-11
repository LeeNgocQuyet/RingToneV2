package com.example.ringtonev2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ringtonev2.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("""DELETE FROM favorites WHERE ringtoneId = :ringtoneId""")
    suspend fun deleteFavoriteById(ringtoneId: String)

    @Query(""" SELECT * FROM favorites ORDER BY title ASC""")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Query("""SELECT EXISTS(SELECT 1 FROM favorites WHERE ringtoneId = :ringtoneId)""")
    fun isFavorite(ringtoneId: String): Flow<Boolean>

    @Query("""SELECT * FROM favorites WHERE ringtoneId = :ringtoneId LIMIT 1""")
    suspend fun getFavoriteById(ringtoneId: String): FavoriteEntity?
}