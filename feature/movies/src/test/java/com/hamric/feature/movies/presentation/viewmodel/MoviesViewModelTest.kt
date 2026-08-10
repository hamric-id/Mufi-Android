package com.hamric.feature.movies.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.hamric.feature.movies.domain.usecase.GetMoviesByGenreUseCase
import com.hamric.feature.movies.presentation.ui.state.MoviesUiState
import com.hamric.feature.movies.utils.CoroutineTestRule
import com.hamric.feature.movies.utils.TestDataFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.delay

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var viewModel: MoviesViewModel
    private val mockUseCase: GetMoviesByGenreUseCase = mockk()
    private val genreId = 28

    @Before
    fun setup() {
        coEvery { mockUseCase.invoke(any()) } returns flowOf(PagingData.from(emptyList()))
        viewModel = MoviesViewModel(mockUseCase)
    }

    @Test
    fun `initial state should be Idle with null movies`() = runTest {

        val uiState = viewModel.uiState.value
        val movies = viewModel.movies.value


        assertIs<MoviesUiState.Idle>(uiState)
        assertNull(movies)
    }

    @Test
    fun `loadMovies should update state to Success with movies when use case succeeds`() = runTest {

        val expectedMovies = TestDataFactory.createMovieList(3)
        val pagingData = PagingData.from(expectedMovies)
        coEvery { mockUseCase.invoke(genreId) } returns flowOf(pagingData)


        viewModel.loadMovies(genreId)

        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Success) {
                delay(10)
            }
        }


        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Success>(uiState)
        assertThat(viewModel.movies.value).isNotNull()
    }

    @Test
    fun `loadMovies should update state to Error when use case throws exception`() = runTest {

        val exception = RuntimeException("Network error")
        coEvery { mockUseCase.invoke(genreId) } throws exception


        viewModel.loadMovies(genreId)


        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Error) {
                delay(10)
            }
        }


        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Error>(uiState)
        assertThat((uiState as MoviesUiState.Error).message)
            .isEqualTo("Unable to load movies. Please try again.")
    }

    @Test
    fun `loadMovies should handle timeout exception with user-friendly message`() = runTest {

        val exception = java.net.SocketTimeoutException("Connection timed out")
        coEvery { mockUseCase.invoke(genreId) } throws exception


        viewModel.loadMovies(genreId)


        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Error) {
                delay(10)
            }
        }


        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Error>(uiState)
        assertThat((uiState as MoviesUiState.Error).message)
            .isEqualTo("Connection timeout. Please try again.")
    }

    @Test
    fun `loadMovies should handle network exception with user-friendly message`() = runTest {

        val exception = java.io.IOException("Network unavailable")
        coEvery { mockUseCase.invoke(genreId) } throws exception


        viewModel.loadMovies(genreId)


        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Error) {
                delay(10)
            }
        }


        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Error>(uiState)
        assertThat((uiState as MoviesUiState.Error).message)
            .isEqualTo("Network error. Please check your connection.")
    }

    @Test
    fun `loadMovies should not reload same genre if already loaded`() = runTest {

        val expectedMovies = TestDataFactory.createMovieList(2)
        val pagingData = PagingData.from(expectedMovies)
        coEvery { mockUseCase.invoke(genreId) } returns flowOf(pagingData)

        viewModel.loadMovies(genreId)
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Success) {
                delay(10)
            }
        }


        viewModel.loadMovies(genreId)
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Success) {
                delay(10)
            }
        }


        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Success>(uiState)
        assertThat(viewModel.movies.value).isNotNull()
    }

    @Test
    fun `refresh should clear existing data and reload`() = runTest {

        val expectedMovies = TestDataFactory.createMovieList(3)
        val pagingData = PagingData.from(expectedMovies)
        coEvery { mockUseCase.invoke(genreId) } returns flowOf(pagingData)


        viewModel.loadMovies(genreId)
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Success) {
                delay(10)
            }
        }

        assertThat(viewModel.movies.value).isNotNull()

        viewModel.refresh()
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Success) {
                delay(10)
            }
        }


        assertThat(viewModel.movies.value).isNotNull()
        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Success>(uiState)
    }

    @Test
    fun `retry should call refresh when not in Error state`() = runTest {

        val expectedMovies = TestDataFactory.createMovieList(2)
        val pagingData = PagingData.from(expectedMovies)
        coEvery { mockUseCase.invoke(genreId) } returns flowOf(pagingData)

        viewModel.loadMovies(genreId)
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Success) {
                delay(10)
            }
        }

        assertIs<MoviesUiState.Success>(viewModel.uiState.value)

        viewModel.retry()
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Success) {
                delay(10)
            }
        }


        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Success>(uiState)
        assertThat(viewModel.movies.value).isNotNull()
    }

    @Test
    fun `clearError should reset Error state to Idle`() = runTest {

        val exception = RuntimeException("Test error")
        coEvery { mockUseCase.invoke(genreId) } throws exception

        viewModel.loadMovies(genreId)
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Error) {
                delay(10)
            }
        }


        assertIs<MoviesUiState.Error>(viewModel.uiState.value)


        viewModel.clearError()
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Idle) {
                delay(10)
            }
        }


        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Idle>(uiState)
    }

    @Test
    fun `loadMovies with invalid genreId should show Error state`() = runTest {

        viewModel.loadMovies(-1)
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Error) {
                delay(10)
            }
        }


        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Error>(uiState)
        assertThat(uiState.message)
            .isEqualTo("Invalid genre ID")
    }

    @Test
    fun `loadMovies with zero genreId should show Error state`() = runTest {

        viewModel.loadMovies(0)
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Error) {
                delay(10)
            }
        }


        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Error>(uiState)
        assertThat(uiState.message)
            .isEqualTo("Invalid genre ID")
    }

    @Test
    fun `multiple loadMovies calls with different genres should switch correctly`() = runTest {

        val genre1 = 28
        val genre2 = 35
        val movies1 = TestDataFactory.createMovieList(2)
        val movies2 = TestDataFactory.createMovieList(3)
        val pagingData1 = PagingData.from(movies1)
        val pagingData2 = PagingData.from(movies2)

        coEvery { mockUseCase.invoke(genre1) } returns flowOf(pagingData1)
        coEvery { mockUseCase.invoke(genre2) } returns flowOf(pagingData2)


        viewModel.loadMovies(genre1)
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Success) {
                delay(10)
            }
        }

        assertThat(viewModel.movies.value).isNotNull()

        viewModel.loadMovies(genre2)
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Success) {
                delay(10)
            }
        }


        assertThat(viewModel.movies.value).isNotNull()
        val uiState = viewModel.uiState.value
        assertIs<MoviesUiState.Success>(uiState)
    }

    @Test
    fun `onCleared should clear movies data`() = runTest {

        val expectedMovies = TestDataFactory.createMovieList(2)
        val pagingData = PagingData.from(expectedMovies)
        coEvery { mockUseCase.invoke(genreId) } returns flowOf(pagingData)

        viewModel.loadMovies(genreId)
        withTimeout(5000) {
            while (viewModel.uiState.value !is MoviesUiState.Success) {
                delay(10)
            }
        }

        assertThat(viewModel.movies.value).isNotNull()


        viewModel.onCleared()


        assertThat(viewModel.movies.value).isNull()
    }
}