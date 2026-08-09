package com.hamric.feature.genres.domain.repository

import com.hamric.core.model.Genre

interface GenreRepository {
    suspend fun getGenres(): Result<List<Genre>>
}