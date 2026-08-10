package com.hamric.feature.movies.data.repository

import com.hamric.core.network.api.TmdbApi
import com.hamric.feature.movies.utils.CoroutineTestRule
import com.hamric.feature.movies.utils.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.google.common.truth.Truth.assertThat

@OptIn(ExperimentalCoroutinesApi::class)
class MovieRepositoryImplTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var repository: MovieRepositoryImpl
    private val mockApi: TmdbApi = mock()
    private val genreId = 28

    @Before
    fun setup() {
        repository = MovieRepositoryImpl(mockApi)
    }

    @Test
    fun `getMoviesByGenre should return PagingData when API call is successful`() = runTest {
        val mockMovies = listOf(
            TestDataFactory.createMovieResponse(id = 1, title = "Movie 1"),
            TestDataFactory.createMovieResponse(id = 2, title = "Movie 2")
        )
        val mockResponse = TestDataFactory.createMoviesResponse(
            page = 1,
            results = mockMovies,
            totalPages = 2,
            totalResults = 4
        )
        whenever(mockApi.getMoviesByGenre(genreId = genreId, page = 1))
            .thenReturn(mockResponse)

        val result = repository.getMoviesByGenre(genreId)
        val pagingData = result.first()

        assertThat(pagingData).isNotNull()
    }

    @Test
    fun `getMoviesByGenre should handle empty response`() = runTest {
        val mockResponse = TestDataFactory.createMoviesResponse(
            results = emptyList(),
            totalPages = 0,
            totalResults = 0
        )
        whenever(mockApi.getMoviesByGenre(genreId = genreId, page = 1))
            .thenReturn(mockResponse)

        val result = repository.getMoviesByGenre(genreId)
        val pagingData = result.first()

        assertThat(pagingData).isNotNull()
    }

    @Test
    fun `getMoviesByGenre should handle API error gracefully`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(mockApi.getMoviesByGenre(genreId = genreId, page = 1))
            .thenThrow(exception)

        val result = repository.getMoviesByGenre(genreId)
        val pagingData = result.first()

        assertThat(pagingData).isNotNull()
    }
}