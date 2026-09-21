package com.goldenankh.purewave.di

import com.goldenankh.domain.helpers.TrackItemIdGenerator
import com.goldenankh.domain.usecases.EditTracksUseCase
import com.goldenankh.domain.usecases.EditTracksUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object EditTracksModule {
    @ViewModelScoped
    @Provides
    fun provideEditTracksUseCase(
        itemIdGenerator: TrackItemIdGenerator
    ): EditTracksUseCase = EditTracksUseCaseImpl(itemIdGenerator)
}