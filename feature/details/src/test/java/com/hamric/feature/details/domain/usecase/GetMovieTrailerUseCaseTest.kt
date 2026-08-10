package com.hamric.feature.details.domain.usecase

import com.hamric.feature.details.domain.repository.MovieDetailRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class GetMovieTrailerUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var useCase: GetMovieTrailerUseCase
    private val mockRepository: MovieDetailRepository = mockk()
    private val movieId = 123

    @Before
    fun setup() {
        useCase = GetMovieTrailerUseCase(mockRepository)
    }

    @Test
    fun `invoke should return trailer when repository returns success`() = runTest {

        val expectedTrailer = TestDataFactory.createVideo()
        coEvery { mockRepository.getMovieTrailer(movieId) } returns Result.success(expectedTrailer)


        val result = useCase(movieId)


        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedTrailer)
    }

    @Test
    fun `invoke should return null when no trailer available`() = runTest {

        coEvery { mockRepository.getMovieTrailer(movieId) } returns Result.success(null)


        val result = useCase(movieId)


        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isNull()
    }

    @Test
    fun `invoke should return failure when repository returns error`() = runTest {

        val exception = RuntimeException("Network error")
        coEvery { mockRepository.getMovieTrailer(movieId) } returns Result.failure(exception)


        val result = useCase(movieId)


        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}