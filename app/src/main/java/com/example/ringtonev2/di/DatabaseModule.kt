package com.example.ringtonev2.di

import android.content.Context
import androidx.room.Room
import com.example.ringtonev2.data.local.AppDatabase
import com.example.ringtonev2.data.local.dao.DownloadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.NAME
        ).build()
    }

    @Provides
    fun provideDownloadDao(db: AppDatabase): DownloadDao {
        return db.downloadDao()
    }
}