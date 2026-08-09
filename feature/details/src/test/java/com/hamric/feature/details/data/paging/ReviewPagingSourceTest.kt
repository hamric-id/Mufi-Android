package com.hamric.feature.details.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hamric.core.model.Review
import com.hamric.core.network.api.TmdbApi
import com.hamric.feature.details.utils.CoroutineTestRule
import com.hamric.feature.details.utils.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.google.common.truth.Truth.assertThat

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewPagingSourceTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var pagingSource: ReviewPagingSource
    private val mockApi: TmdbApi = mock()
    private val movieId = 123

    @Before
    fun setup() {
        pagingSource = ReviewPagingSource(mockApi, movieId)
    }

    @Test
    fun `load should return success with reviews for first page`() = runTest {

        val mockReviews = listOf(
            TestDataFactory.createReviewResponse(id = "1", author = "User 1"),
            TestDataFactory.createReviewResponse(id = "2", author = "User 2")
        )
        val mockResponse = TestDataFactory.createReviewsResponse(
            page = 1,
            results = mockReviews,
            totalPages = 3,
            totalResults = 6
        )
        whenever(mockApi.getMovieReviews(movieId = movieId, page = 1))
            .thenReturn(mockResponse)


        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )


        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val pageResult = result as PagingSource.LoadResult.Page<Int, Review>
        assertThat(pageResult.data).hasSize(2)
        assertThat(pageResult.data[0].author).isEqualTo("User 1")
        assertThat(pageResult.data[1].author).isEqualTo("User 2")
        assertThat(pageResult.prevKey).isNull()
        assertThat(pageResult.nextKey).isEqualTo(2)
    }

    @Test
    fun `load should return success with reviews for second page`() = runTest {

        val mockReviews = listOf(
            TestDataFactory.createReviewResponse(id = "3", author = "User 3"),
            TestDataFactory.createReviewResponse(id = "4", author = "User 4")
        )
        val mockResponse = TestDataFactory.createReviewsResponse(
            page = 2,
            results = mockReviews,
            totalPages = 3,
            totalResults = 6
        )
        whenever(mockApi.getMovieReviews(movieId = movieId, page = 2))
            .thenReturn(mockResponse)


        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 2,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )


        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val pageResult = result as PagingSource.LoadResult.Page<Int, Review>
        assertThat(pageResult.data).hasSize(2)
        assertThat(pageResult.data[0].author).isEqualTo("User 3")
        assertThat(pageResult.prevKey).isEqualTo(1)
        assertThat(pageResult.nextKey).isEqualTo(3)
    }

    @Test
    fun `load should return error when API throws exception`() = runTest {

        val exception = RuntimeException("Network error")
        whenever(mockApi.getMovieReviews(movieId = movieId, page = 1))
            .thenThrow(exception)


        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )


        assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class.java)
        val errorResult = result as PagingSource.LoadResult.Error<Int, Review>
        assertThat(errorResult.throwable).isEqualTo(exception)
    }

    @Test
    fun `load should handle empty page`() = runTest {

        val mockResponse = TestDataFactory.createReviewsResponse(
            page = 1,
            results = emptyList(),
            totalPages = 1,
            totalResults = 0
        )
        whenever(mockApi.getMovieReviews(movieId = movieId, page = 1))
            .thenReturn(mockResponse)


        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )


        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val pageResult = result as PagingSource.LoadResult.Page<Int, Review>
        assertThat(pageResult.data).isEmpty()
    }

    @Test
    fun `getRefreshKey should return valid key`() {

        val page = PagingSource.LoadResult.Page(
            data = emptyList<Review>(),
            prevKey = 1,
            nextKey = 3
        )

        val state = PagingState<Int, Review>(
            pages = listOf(page),
            anchorPosition = 10,
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 10,
                enablePlaceholders = false
            ),
            leadingPlaceholderCount = 0
        )


        val refreshKey = pagingSource.getRefreshKey(state)


        assertThat(refreshKey).isIn(listOf(null, 2))
    }
}