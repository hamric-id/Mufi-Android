package com.hamric.feature.details.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hamric.core.model.Movie
import com.hamric.core.model.Review
import com.hamric.core.model.Video
import com.hamric.core.network.api.TmdbApi
import com.hamric.core.network.mapper.toDomainModel
import com.hamric.core.network.mapper.getTrailer
import com.hamric.feature.details.data.paging.ReviewPagingSource
import com.hamric.feature.details.domain.repository.MovieDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class MovieDetailRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : MovieDetailRepository {

    override fun getMovieDetails(movieId: Int): Flow<Result<Movie>> = flow {
        try {
            val response = api.getMovieDetails(movieId = movieId)
            emit(Result.success(response.toDomainModel()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getMovieReviews(movieId: Int): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ReviewPagingSource(api, movieId) }
        ).flow
    }

    override fun getMovieTrailer(movieId: Int): Flow<Result<Video?>> = flow {
        try {
            val response = api.getMovieVideos(movieId = movieId)
            val trailer = response.results.getTrailer()
            emit(Result.success(trailer))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}


