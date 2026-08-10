package com.hamric.feature.details.domain.usecase

import com.hamric.core.model.Video
import com.hamric.feature.details.domain.repository.MovieDetailRepository
import javax.inject.Inject


class GetMovieTrailerUseCase @Inject constructor(
    private val repository: MovieDetailRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Video?> {
        return repository.getMovieTrailer(movieId)
    }
}