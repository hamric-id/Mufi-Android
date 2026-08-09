package com.hamric.feature.movies.data.paging

import androidx.paging.PagingSource
import com.hamric.core.network.api.TmdbApi
import com.hamric.feature.movies.utils.CoroutineTestRule
import com.hamric.feature.movies.utils.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.google.common.truth.Truth.assertThat

@OptIn(ExperimentalCoroutinesApi::class)
class MoviePagingSourceTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var pagingSource: MoviePagingSource
    private val mockApi: TmdbApi = mock()
    private val genreId = 28

    @Before
    fun setup() {
        pagingSource = MoviePagingSource(mockApi, genreId)
    }

    @Test
    fun `load should return success with movies for first page`() = runTest {
        val mockMovies = listOf(
            TestDataFactory.createMovieResponse(id = 1, title = "Movie 1"),
            TestDataFactory.createMovieResponse(id = 2, title = "Movie 2")
        )
        val mockResponse = TestDataFactory.createMoviesResponse(
            page = 1,
            results = mockMovies,
            totalPages = 3,
            totalResults = 6
        )
        whenever(mockApi.getMoviesByGenre(genreId = genreId, page = 1))
            .thenReturn(mockResponse)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val pageResult = result as PagingSource.LoadResult.Page
        assertThat(pageResult.data).hasSize(2)
        assertThat(pageResult.data[0].title).isEqualTo("Movie 1")
        assertThat(pageResult.nextKey).isEqualTo(2)
    }

    @Test
    fun `load should return error when API throws exception`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(mockApi.getMoviesByGenre(genreId = genreId, page = 1))
            .thenThrow(exception)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class.java)
        val errorResult = result as PagingSource.LoadResult.Error
        assertThat(errorResult.throwable).isEqualTo(exception)
    }
}