/*
The MIT License

Copyright (c) 2026 DigitalWorldPure

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.goldenankh.purewave.di

import com.goldenankh.data.repository.SampleRepositoryImpl
import com.goldenankh.domain.usecases.samples.DeleteSampleUseCase
import com.goldenankh.domain.usecases.samples.DeleteSampleUseCaseImpl
import com.goldenankh.domain.usecases.samples.GetSamplesCountUseCase
import com.goldenankh.domain.usecases.samples.GetSamplesCountUseCaseImpl
import com.goldenankh.domain.usecases.samples.ObserveSampleUseCase
import com.goldenankh.domain.usecases.samples.ObserveSampleUseCaseImpl
import com.goldenankh.domain.usecases.samples.ObserveSamplesUseCase
import com.goldenankh.domain.usecases.samples.ObserveSamplesUseCaseImpl
import com.goldenankh.domain.usecases.samples.SaveSampleUseCase
import com.goldenankh.domain.usecases.samples.SaveSampleUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object SamplesModule {
    @ViewModelScoped
    @Provides
    fun provideGetCurvesCountUseCase(
        samplesRepository: SampleRepositoryImpl
    ): GetSamplesCountUseCase = GetSamplesCountUseCaseImpl(samplesRepository)

    @ViewModelScoped
    @Provides
    fun provideDeleteSampleUseCase(
        samplesRepository: SampleRepositoryImpl
    ): DeleteSampleUseCase = DeleteSampleUseCaseImpl(samplesRepository)

    @ViewModelScoped
    @Provides
    fun provideObserveSamplesUseCase(
        samplesRepository: SampleRepositoryImpl
    ): ObserveSamplesUseCase = ObserveSamplesUseCaseImpl(samplesRepository)

    @ViewModelScoped
    @Provides
    fun provideObserveSampleUseCase(
        samplesRepository: SampleRepositoryImpl
    ): ObserveSampleUseCase = ObserveSampleUseCaseImpl(samplesRepository)

    @ViewModelScoped
    @Provides
    fun provideSaveSampleUseCase(
        samplesRepository: SampleRepositoryImpl
    ): SaveSampleUseCase = SaveSampleUseCaseImpl(samplesRepository)
}