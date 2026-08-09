package com.hamric.feature.details.di

import com.hamric.feature.details.data.repository.MovieDetailRepositoryImpl
import com.hamric.feature.details.domain.repository.MovieDetailRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MovieDetailModule {

    @Binds
    @Singleton
    abstract fun bindMovieDetailRepository(
        impl: MovieDetailRepositoryImpl
    ): MovieDetailRepository
}