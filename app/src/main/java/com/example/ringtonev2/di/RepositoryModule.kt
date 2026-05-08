package com.example.ringtonev2.di

import com.example.ringtonev2.data.repository.RingtoneRepositoryImpl
import com.example.ringtonev2.domain.RingtoneRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRingtoneRepository(
        ringtoneRepositoryImpl: RingtoneRepositoryImpl
    ): RingtoneRepository
}