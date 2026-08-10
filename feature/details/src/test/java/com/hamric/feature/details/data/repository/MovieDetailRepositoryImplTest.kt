package com.hamric.feature.details.data.repository

import com.hamric.core.network.api.TmdbApi
import com.hamric.feature.details.utils.CoroutineTestRule
import com.hamric.feature.details.utils.TestDataFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailRepositoryImplTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var repository: MovieDetailRepositoryImpl
    private val mockApi: TmdbApi = mockk()
    private val movieId = 123

    @Before
    fun setup() {
        repository = MovieDetailRepositoryImpl(mockApi)
    }


    @Test
    fun `getMovieDetails should return failure when API throws exception`() = runTest {

        val exception = RuntimeException("Network error")
        coEvery { mockApi.getMovieDetails(movieId) } throws exception


        val results = repository.getMovieDetails(movieId).take(1).toList()


        assertThat(results).isNotEmpty()
        val result = results.first()
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `getMovieReviews should return PagingData when API call is successful`() = runTest {

        val mockReviews = listOf(
            TestDataFactory.createReviewResponse(id = "1", author = "User 1"),
            TestDataFactory.createReviewResponse(id = "2", author = "User 2")
        )
        val mockResponse = TestDataFactory.createReviewsResponse(
            page = 1,
            results = mockReviews,
            totalPages = 2,
            totalResults = 4
        )
        coEvery { mockApi.getMovieReviews(movieId = movieId, page = 1) } returns mockResponse


        val pagingData = repository.getMovieReviews(movieId).take(1).toList().firstOrNull()


        assertThat(pagingData).isNotNull()
    }

    @Test
    fun `getMovieReviews should handle empty response`() = runTest {

        val mockResponse = TestDataFactory.createReviewsResponse(
            results = emptyList(),
            totalPages = 0,
            totalResults = 0
        )
        coEvery { mockApi.getMovieReviews(movieId = movieId, page = 1) } returns mockResponse


        val pagingData = repository.getMovieReviews(movieId).take(1).toList().firstOrNull()


        assertThat(pagingData).isNotNull()
    }





    @Test
    fun `getMovieTrailer should return failure when API throws exception`() = runTest {

        val exception = RuntimeException("Network error")
        coEvery { mockApi.getMovieVideos(movieId) } throws exception


        val results = repository.getMovieTrailer(movieId).take(1).toList()


        assertThat(results).isNotEmpty()
        val result = results.first()
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}