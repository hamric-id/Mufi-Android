package com.hamric.feature.movies.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hamric.core.model.Movie
import com.hamric.core.network.api.TmdbApi
import com.hamric.feature.movies.data.paging.DeduplicatingPagingSource
import com.hamric.feature.movies.data.paging.MoviePagingSource
import com.hamric.feature.movies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : MovieRepository {

    override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DeduplicatingPagingSource(
                    MoviePagingSource(api, genreId)
                )
            }
        ).flow
    }
}

