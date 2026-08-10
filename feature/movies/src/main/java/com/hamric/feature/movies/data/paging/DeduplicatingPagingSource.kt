package com.hamric.feature.movies.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hamric.core.model.Movie

class DeduplicatingPagingSource(
    private val source: PagingSource<Int, Movie>
) : PagingSource<Int, Movie>() {

    private val seenIds = mutableSetOf<Int>()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val result = source.load(params)

        return when (result) {
            is LoadResult.Page -> {
                val uniqueMovies = result.data.filter { movie ->
                    movie.id !in seenIds
                }

                uniqueMovies.forEach { movie ->
                    seenIds.add(movie.id)
                }

                LoadResult.Page(
                    data = uniqueMovies,
                    prevKey = result.prevKey,
                    nextKey = result.nextKey
                )
            }
            is LoadResult.Error -> result
            is LoadResult.Invalid -> result
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return source.getRefreshKey(state)
    }
}