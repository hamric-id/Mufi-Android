package com.hamric.feature.details.domain.repository

import androidx.paging.PagingData
import com.hamric.core.model.Movie
import com.hamric.core.model.Review
import com.hamric.core.model.Video
import kotlinx.coroutines.flow.Flow

interface MovieDetailRepository {
    suspend fun getMovieDetails(movieId: Int): Result<Movie>
    suspend fun getMovieTrailer(movieId: Int): Result<Video?>
    fun getMovieReviews(movieId: Int): Flow<PagingData<Review>>
}