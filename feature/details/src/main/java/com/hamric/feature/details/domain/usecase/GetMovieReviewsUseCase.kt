package com.hamric.feature.details.domain.usecase

import androidx.paging.PagingData
import com.hamric.core.model.Review
import com.hamric.feature.details.domain.repository.MovieDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieReviewsUseCase @Inject constructor(
    private val repository: MovieDetailRepository
) {
    operator fun invoke(movieId: Int): Flow<PagingData<Review>> {
        return repository.getMovieReviews(movieId)
    }
}