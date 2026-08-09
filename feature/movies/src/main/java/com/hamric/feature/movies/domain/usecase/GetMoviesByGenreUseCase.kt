package com.hamric.feature.movies.domain.usecase

import androidx.paging.PagingData
import com.hamric.core.model.Movie
import com.hamric.feature.movies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetMoviesByGenreUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(genreId: Int): Flow<PagingData<Movie>> {
        return repository.getMoviesByGenre(genreId)
    }
}
