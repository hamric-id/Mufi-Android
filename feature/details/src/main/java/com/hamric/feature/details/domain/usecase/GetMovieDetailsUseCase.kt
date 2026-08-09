package com.hamric.feature.details.domain.usecase

import com.hamric.core.model.Movie
import com.hamric.feature.details.domain.repository.MovieDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieDetailRepository
) {
    operator fun invoke(movieId: Int): Flow<Result<Movie>> {
        return repository.getMovieDetails(movieId)
    }
}