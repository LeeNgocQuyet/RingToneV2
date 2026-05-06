package com.example.ringtonev2.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ringtonev2.data.local.dao.DownloadDao
import com.example.ringtonev2.data.local.entity.DownloadEntity



@Database(
    entities = [
        DownloadEntity::class,

    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao

    companion object {
        const val NAME = "ringtone.db"
    }
}