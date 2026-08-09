package com.hamric.feature.movies.domain.repository

import androidx.paging.PagingData
import com.hamric.core.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>>
}