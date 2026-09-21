package com.goldenankh.domain.di

import com.goldenankh.domain.helpers.TrackItemIdGenerator
import com.goldenankh.domain.helpers.TrackItemIdGeneratorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    @Provides
    @Singleton
    fun provideTrackIdGenerator(): TrackItemIdGenerator = TrackItemIdGeneratorImpl()
}