package com.hamric.feature.details.domain.repository

import androidx.paging.PagingData
import com.hamric.core.model.Movie
import com.hamric.core.model.Review
import com.hamric.core.model.Video
import kotlinx.coroutines.flow.Flow


interface MovieDetailRepository {
    fun getMovieDetails(movieId: Int): Flow<Result<Movie>>

    fun getMovieReviews(movieId: Int): Flow<PagingData<Review>>

    fun getMovieTrailer(movieId: Int): Flow<Result<Video?>>
}