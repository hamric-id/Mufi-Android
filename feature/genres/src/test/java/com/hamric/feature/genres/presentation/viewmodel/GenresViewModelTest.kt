package com.hamric.feature.genres.presentation.viewmodel

import app.cash.turbine.test
import com.hamric.core.model.Genre
import com.hamric.feature.genres.domain.usecase.GetGenresUseCase
import com.hamric.feature.genres.presentation.state.GenresUiState
import com.hamric.feature.genres.utils.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class GenresViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var viewModel: GenresViewModel
    private val mockUseCase: GetGenresUseCase = mockk()

    @Before
    fun setup() {
        coEvery { mockUseCase() } returns Result.success(emptyList())
        viewModel = GenresViewModel(mockUseCase)
    }


    @Test
    fun `loadGenres should update state to Success with genres when use case succeeds`() = runTest {

        val expectedGenres = listOf(
            Genre(id = 28, name = "Action"),
            Genre(id = 35, name = "Comedy"),
            Genre(id = 18, name = "Drama")
        )
        coEvery { mockUseCase() } returns Result.success(expectedGenres)


        viewModel = GenresViewModel(mockUseCase)
        advanceUntilIdle()


        viewModel.uiState.test {
            val state = awaitItem()
            assertIs<GenresUiState.Success>(state)
            assertThat(state.genres).isEqualTo(expectedGenres)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadGenres should update state to Empty when use case returns empty list`() = runTest {

        coEvery { mockUseCase() } returns Result.success(emptyList())


        viewModel = GenresViewModel(mockUseCase)
        advanceUntilIdle()


        viewModel.uiState.test {
            val state = awaitItem()
            assertIs<GenresUiState.Empty>(state)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadGenres should update state to Error when use case fails`() = runTest {

        val errorMessage = "Unable to load genres. Please check your connection."
        val exception = Exception("Network error")
        coEvery { mockUseCase() } returns Result.failure(exception)


        viewModel = GenresViewModel(mockUseCase)
        advanceUntilIdle()


        viewModel.uiState.test {
            val state = awaitItem()
            assertIs<GenresUiState.Error>(state)
            assertThat(state.message).isEqualTo(errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refresh should trigger reload and update state`() = runTest {

        val genres = listOf(Genre(id = 28, name = "Action"))
        coEvery { mockUseCase() } returns Result.success(genres)


        viewModel = GenresViewModel(mockUseCase)
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()


        viewModel.uiState.test {
            val state = awaitItem()
            assertIs<GenresUiState.Success>(state)
            assertThat(state.genres).isEqualTo(genres)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `retry should call refresh`() = runTest {

        val genres = listOf(Genre(id = 28, name = "Action"))
        coEvery { mockUseCase() } returns Result.success(genres)


        viewModel = GenresViewModel(mockUseCase)
        advanceUntilIdle()


        viewModel.retry()
        advanceUntilIdle()


        viewModel.uiState.test {
            val state = awaitItem()
            assertIs<GenresUiState.Success>(state)
            assertThat(state.genres).isEqualTo(genres)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple rapid refreshes should not cause issues`() = runTest {

        val genres = listOf(Genre(id = 28, name = "Action"))
        coEvery { mockUseCase() } returns Result.success(genres)


        viewModel = GenresViewModel(mockUseCase)
        advanceUntilIdle()


        viewModel.refresh()
        viewModel.refresh()
        viewModel.refresh()
        advanceUntilIdle()


        viewModel.uiState.test {
            val state = awaitItem()
            assertIs<GenresUiState.Success>(state)
            assertThat(state.genres).isEqualTo(genres)
            cancelAndIgnoreRemainingEvents()
        }
    }
}