package com.hamric.feature.details.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hamric.core.model.Review
import com.hamric.core.network.api.TmdbApi
import com.hamric.core.network.mapper.toDomainModels

class ReviewPagingSource(
    private val api: TmdbApi,
    private val movieId: Int
) : PagingSource<Int, Review>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        return try {
            val page = params.key ?: 1
            val response = api.getMovieReviews(movieId = movieId, page = page)

            LoadResult.Page(
                data = response.results.toDomainModels(),
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (page < response.totalPages) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}