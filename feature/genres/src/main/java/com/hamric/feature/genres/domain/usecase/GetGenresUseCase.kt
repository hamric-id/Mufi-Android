package com.hamric.feature.genres.domain.usecase

import com.hamric.core.model.Genre
import com.hamric.feature.genres.domain.repository.GenreRepository
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val repository: GenreRepository
) {
    suspend operator fun invoke(): Result<List<Genre>> {
        return repository.getGenres()
    }
}