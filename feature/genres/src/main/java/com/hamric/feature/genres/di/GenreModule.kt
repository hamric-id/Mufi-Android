package com.hamric.feature.genres.di

import com.hamric.feature.genres.data.repository.GenreRepositoryImpl
import com.hamric.feature.genres.domain.repository.GenreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GenreModule {

    @Binds
    @Singleton
    abstract fun bindGenreRepository(
        impl: GenreRepositoryImpl
    ): GenreRepository
}