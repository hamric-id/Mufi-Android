package com.hamric.feature.details.domain.usecase

import com.hamric.core.model.Video
import com.hamric.feature.details.domain.repository.MovieDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieTrailerUseCase @Inject constructor(
    private val repository: MovieDetailRepository
) {
    operator fun invoke(movieId: Int): Flow<Result<Video?>> {
        return repository.getMovieTrailer(movieId)
    }
}