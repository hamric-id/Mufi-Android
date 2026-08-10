package com.hamric.feature.details.domain.usecase

import com.hamric.core.model.Movie
import com.hamric.feature.details.domain.repository.MovieDetailRepository
import javax.inject.Inject


class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieDetailRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Movie> {
        return repository.getMovieDetails(movieId)
    }
}

