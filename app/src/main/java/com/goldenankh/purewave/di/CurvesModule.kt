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

import com.goldenankh.data.repository.CurveRepositoryImpl
import com.goldenankh.domain.usecases.curves.DeleteCurveUseCase
import com.goldenankh.domain.usecases.curves.DeleteCurveUseCaseImpl
import com.goldenankh.domain.usecases.curves.GetCurveUseCase
import com.goldenankh.domain.usecases.curves.GetCurveUseCaseImpl
import com.goldenankh.domain.usecases.curves.GetCurvesCountUseCase
import com.goldenankh.domain.usecases.curves.GetCurvesCountUseCaseImpl
import com.goldenankh.domain.usecases.curves.ObserveCurvesUseCase
import com.goldenankh.domain.usecases.curves.ObserveCurvesUseCaseImpl
import com.goldenankh.domain.usecases.curves.SaveCurveUseCase
import com.goldenankh.domain.usecases.curves.SaveCurveUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object CurvesModule {
    @ViewModelScoped
    @Provides
    fun provideGetCurvesCountUseCase(
        curvesRepository: CurveRepositoryImpl
    ): GetCurvesCountUseCase = GetCurvesCountUseCaseImpl(curvesRepository)

    @ViewModelScoped
    @Provides
    fun provideDeleteCurveUseCase(
        curvesRepository: CurveRepositoryImpl
    ): DeleteCurveUseCase = DeleteCurveUseCaseImpl(curvesRepository)

    @ViewModelScoped
    @Provides
    fun provideGetCurveUseCase(
        curvesRepository: CurveRepositoryImpl
    ): GetCurveUseCase = GetCurveUseCaseImpl(curvesRepository)

    @ViewModelScoped
    @Provides
    fun provideObserveCurvesUseCase(
        curvesRepository: CurveRepositoryImpl
    ): ObserveCurvesUseCase = ObserveCurvesUseCaseImpl(curvesRepository)

    @ViewModelScoped
    @Provides
    fun provideSaveCurveUseCase(
        curvesRepository: CurveRepositoryImpl
    ): SaveCurveUseCase = SaveCurveUseCaseImpl(curvesRepository)
}