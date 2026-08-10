package com.hamric.feature.details.data.repository

import com.hamric.core.network.api.TmdbApi
import com.hamric.feature.details.utils.CoroutineTestRule
import com.hamric.feature.details.utils.TestDataFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first

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
    fun `getMovieDetails should return movie when API call is successful`() = runTest {

        val mockResponse = TestDataFactory.createMovieResponse(id = movieId, title = "Test Movie")
        coEvery { mockApi.getMovieDetails(movieId) } returns mockResponse


        val result = repository.getMovieDetails(movieId)


        assertThat(result.isSuccess).isTrue()
        val movie = result.getOrNull()
        assertThat(movie).isNotNull()
        assertThat(movie?.id).isEqualTo(movieId)
        assertThat(movie?.title).isEqualTo("Test Movie")
    }

    @Test
    fun `getMovieDetails should return failure when API throws exception`() = runTest {

        val exception = RuntimeException("Network error")
        coEvery { mockApi.getMovieDetails(movieId) } throws exception


        val result = repository.getMovieDetails(movieId)


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


        val result = repository.getMovieReviews(movieId)
        val pagingData = result.first()


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


        val result = repository.getMovieReviews(movieId)
        val pagingData = result.first()


        assertThat(pagingData).isNotNull()
    }

    @Test
    fun `getMovieTrailer should return trailer when available`() = runTest {

        val mockTrailer = TestDataFactory.createVideoResponse(
            key = "abc123",
            name = "Official Trailer",
            site = "YouTube",
            type = "Trailer"
        )
        val mockResponse = TestDataFactory.createVideosResponse(results = listOf(mockTrailer))
        coEvery { mockApi.getMovieVideos(movieId) } returns mockResponse


        val result = repository.getMovieTrailer(movieId)


        assertThat(result.isSuccess).isTrue()
        val trailer = result.getOrNull()
        assertThat(trailer).isNotNull()
        assertThat(trailer?.key).isEqualTo("abc123")
    }

    @Test
    fun `getMovieTrailer should return null when no trailer available`() = runTest {

        val mockResponse = TestDataFactory.createVideosResponse(results = emptyList())
        coEvery { mockApi.getMovieVideos(movieId) } returns mockResponse


        val result = repository.getMovieTrailer(movieId)


        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isNull()
    }

    @Test
    fun `getMovieTrailer should return failure when API throws exception`() = runTest {

        val exception = RuntimeException("Network error")
        coEvery { mockApi.getMovieVideos(movieId) } throws exception


        val result = repository.getMovieTrailer(movieId)


        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}