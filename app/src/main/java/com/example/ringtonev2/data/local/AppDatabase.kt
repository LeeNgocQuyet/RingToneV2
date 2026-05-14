package com.example.ringtonev2.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ringtonev2.data.local.dao.DownloadDao
import com.example.ringtonev2.data.local.dao.FavoriteDao
import com.example.ringtonev2.data.local.dao.RingtoneDao
import com.example.ringtonev2.data.local.entity.DownloadedRingtone
import com.example.ringtonev2.data.local.entity.FavoriteEntity
import com.example.ringtonev2.data.local.entity.RingtoneEntity



@Database(
    entities = [
        DownloadedRingtone::class,
        FavoriteEntity::class,
        RingtoneEntity::class
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun ringtoneDao(): RingtoneDao
    companion object {
        const val NAME = "ringtone.db"
    }
}