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
import javax.inject.Inject


class MovieDetailRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : MovieDetailRepository {

    override suspend fun getMovieDetails(movieId: Int): Result<Movie> {
        return try {
            val response = api.getMovieDetails(movieId = movieId)
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovieTrailer(movieId: Int): Result<Video?> {
        return try {
            val response = api.getMovieVideos(movieId = movieId)
            val trailer = response.results.getTrailer()
            Result.success(trailer)
        } catch (e: Exception) {
            Result.failure(e)
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
}


