package com.hamric.feature.details.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.hamric.feature.details.domain.usecase.GetMovieDetailsUseCase
import com.hamric.feature.details.domain.usecase.GetMovieReviewsUseCase
import com.hamric.feature.details.domain.usecase.GetMovieTrailerUseCase
import com.hamric.feature.details.presentation.state.MovieDetailUiState
import com.hamric.feature.details.presentation.state.ReviewsState
import com.hamric.feature.details.utils.CoroutineTestRule
import com.hamric.feature.details.utils.TestDataFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var viewModel: MovieDetailViewModel
    private val mockGetDetailsUseCase: GetMovieDetailsUseCase = mockk()
    private val mockGetReviewsUseCase: GetMovieReviewsUseCase = mockk()
    private val mockGetTrailerUseCase: GetMovieTrailerUseCase = mockk()
    private val movieId = 123

    @Before
    fun setup() {
        coEvery { mockGetDetailsUseCase.invoke(any()) } returns Result.success(TestDataFactory.createMovie())
        coEvery { mockGetTrailerUseCase.invoke(any()) } returns Result.success(null)
        coEvery { mockGetReviewsUseCase.invoke(any()) } returns flowOf(PagingData.from(emptyList()))

        viewModel = MovieDetailViewModel(
            getMovieDetailsUseCase = mockGetDetailsUseCase,
            getMovieReviewsUseCase = mockGetReviewsUseCase,
            getMovieTrailerUseCase = mockGetTrailerUseCase
        )
    }

    @Test
    fun `initial state should be Loading with null reviews and false refreshing`() = runTest {
        val uiState = viewModel.uiState.value
        val reviews = viewModel.reviews.value
        val reviewsState = viewModel.reviewsState.value
        val isRefreshing = viewModel.isRefreshing.value

        assertIs<MovieDetailUiState.Loading>(uiState)
        assertNull(reviews)
        assertIs<ReviewsState.Loading>(reviewsState)
        assertThat(isRefreshing).isFalse()
    }

    @Test
    fun `loadMovieDetails should update state to Success with movie and trailer`() = runTest {
        val expectedMovie = TestDataFactory.createMovie(id = movieId)
        val expectedTrailer = TestDataFactory.createVideo()
        coEvery { mockGetDetailsUseCase.invoke(movieId) } returns Result.success(expectedMovie)
        coEvery { mockGetTrailerUseCase.invoke(movieId) } returns Result.success(expectedTrailer)

        viewModel.loadMovieDetails(movieId)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<MovieDetailUiState.Success>(state)
        val successState = state
        assertThat(successState.movie).isEqualTo(expectedMovie)
        assertThat(successState.trailer).isEqualTo(expectedTrailer)
    }

    @Test
    fun `loadMovieDetails should update state to Success with movie but no trailer`() = runTest {
        val expectedMovie = TestDataFactory.createMovie(id = movieId)
        coEvery { mockGetDetailsUseCase.invoke(movieId) } returns Result.success(expectedMovie)
        coEvery { mockGetTrailerUseCase.invoke(movieId) } returns Result.success(null)

        viewModel.loadMovieDetails(movieId)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<MovieDetailUiState.Success>(state)
        val successState = state
        assertThat(successState.movie).isEqualTo(expectedMovie)
        assertNull(successState.trailer)
    }

    @Test
    fun `loadMovieDetails should update state to Error when details fetch fails`() = runTest {
        val exception = RuntimeException("Failed to load movie")
        coEvery { mockGetDetailsUseCase.invoke(movieId) } returns Result.failure(exception)
        coEvery { mockGetTrailerUseCase.invoke(movieId) } returns Result.success(null)

        viewModel.loadMovieDetails(movieId)
        repeat(5) { advanceUntilIdle() }

        val state = viewModel.uiState.value
        assertIs<MovieDetailUiState.Error>(state)
        val errorState = state
        assertThat(errorState.message).contains("Unable to load movie details")
    }

    @Test
    fun `loadMovieDetails should show Success when details succeed even if trailer fails`() = runTest {
        val expectedMovie = TestDataFactory.createMovie(id = movieId)
        val exception = RuntimeException("Trailer failed")
        coEvery { mockGetDetailsUseCase.invoke(movieId) } returns Result.success(expectedMovie)
        coEvery { mockGetTrailerUseCase.invoke(movieId) } returns Result.failure(exception)

        viewModel.loadMovieDetails(movieId)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<MovieDetailUiState.Success>(state)
        val successState = state
        assertThat(successState.movie).isEqualTo(expectedMovie)
        assertNull(successState.trailer)
    }

    @Test
    fun `loadReviews should update reviews Flow when successful`() = runTest {
        val expectedReviews = TestDataFactory.createReviewList(3)
        val pagingData = PagingData.from(expectedReviews)
        coEvery { mockGetReviewsUseCase.invoke(movieId) } returns flowOf(pagingData)

        viewModel.loadReviews(movieId)
        advanceUntilIdle()

        assertThat(viewModel.reviews.value).isNotNull()
        val reviewsState = viewModel.reviewsState.value
        assertIs<ReviewsState.Success>(reviewsState)
    }

    @Test
    fun `loadReviews should update reviewsState to Error when fails`() = runTest {
        val exception = RuntimeException("Failed to load reviews")
        coEvery { mockGetReviewsUseCase.invoke(movieId) } throws exception

        viewModel.loadReviews(movieId)
        repeat(5) { advanceUntilIdle() }

        val reviewsState = viewModel.reviewsState.value
        assertIs<ReviewsState.Error>(reviewsState)
        val errorState = reviewsState
        assertThat(errorState.message).contains("Unable to load reviews")
        assertNull(viewModel.reviews.value)
    }


    @Test
    fun `refresh should trigger refresh and update state`() = runTest {
        val expectedMovie = TestDataFactory.createMovie(id = movieId)
        val expectedTrailer = TestDataFactory.createVideo()
        coEvery { mockGetDetailsUseCase.invoke(movieId) } returns Result.success(expectedMovie)
        coEvery { mockGetTrailerUseCase.invoke(movieId) } returns Result.success(expectedTrailer)

        viewModel.loadMovieDetails(movieId)
        advanceUntilIdle()

        assertIs<MovieDetailUiState.Success>(viewModel.uiState.value)

        viewModel.refresh()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<MovieDetailUiState.Success>(state)
        val successState = state
        assertThat(successState.movie).isEqualTo(expectedMovie)
    }


    @Test
    fun `retry should reload movie details`() = runTest {
        val exception = RuntimeException("Failed to load movie")
        coEvery { mockGetDetailsUseCase.invoke(movieId) } returns Result.failure(exception)
        coEvery { mockGetTrailerUseCase.invoke(movieId) } returns Result.success(null)

        viewModel.loadMovieDetails(movieId)
        repeat(5) { advanceUntilIdle() }

        assertIs<MovieDetailUiState.Error>(viewModel.uiState.value)

        val expectedMovie = TestDataFactory.createMovie(id = movieId)
        coEvery { mockGetDetailsUseCase.invoke(movieId) } returns Result.success(expectedMovie)

        viewModel.retry()
        repeat(5) { advanceUntilIdle() }

        val state = viewModel.uiState.value
        assertIs<MovieDetailUiState.Success>(state)
        val successState = state
        assertThat(successState.movie).isEqualTo(expectedMovie)
    }

    @Test
    fun `loadMovieDetails with invalid movieId should show Error state`() = runTest {
        viewModel.loadMovieDetails(-1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<MovieDetailUiState.Error>(state)
        val errorState = state
        assertThat(errorState.message).isEqualTo("Invalid movie ID")
    }


    @Test
    fun `onCleared should clear reviews data`() = runTest {
        val expectedReviews = TestDataFactory.createReviewList(2)
        val pagingData = PagingData.from(expectedReviews)
        coEvery { mockGetReviewsUseCase.invoke(movieId) } returns flowOf(pagingData)

        viewModel.loadReviews(movieId)
        advanceUntilIdle()

        assertThat(viewModel.reviews.value).isNotNull()

        viewModel.onCleared()

        assertThat(viewModel.reviews.value).isNull()
    }
}