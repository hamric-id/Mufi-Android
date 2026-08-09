package com.hamric.feature.details.domain.usecase

import com.hamric.feature.details.domain.repository.MovieDetailRepository
import com.hamric.feature.details.utils.CoroutineTestRule
import com.hamric.feature.details.utils.TestDataFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
class GetMovieDetailsUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var useCase: GetMovieDetailsUseCase
    private val mockRepository: MovieDetailRepository = mockk()
    private val movieId = 123

    @Before
    fun setup() {
        useCase = GetMovieDetailsUseCase(mockRepository)
    }

    @Test
    fun `invoke should return movie details when repository returns success`() = runTest {

        val expectedMovie = TestDataFactory.createMovie(id = movieId)
        coEvery { mockRepository.getMovieDetails(movieId) } returns flowOf(Result.success(expectedMovie))


        val result = useCase(movieId).first()


        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedMovie)
    }

    @Test
    fun `invoke should return failure when repository returns error`() = runTest {

        val exception = RuntimeException("Network error")
        coEvery { mockRepository.getMovieDetails(movieId) } returns flowOf(Result.failure(exception))


        val result = useCase(movieId).first()


        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}