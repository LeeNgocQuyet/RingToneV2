package com.example.ringtonev2.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ringtonev2.data.local.dao.FavoriteDao
import com.example.ringtonev2.data.local.dao.DownloadedRingtoneDao
import com.example.ringtonev2.data.local.entity.FavoriteEntity
import com.example.ringtonev2.data.local.entity.DownloadedRingtoneEntity



@Database(
    entities = [
        FavoriteEntity::class,
        DownloadedRingtoneEntity::class
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun downloadedRingtoneDao(): DownloadedRingtoneDao
    companion object {
        const val NAME = "ringtone.db"
    }
}
