package com.hamric.feature.genres.domain.usecase

import com.hamric.core.model.Genre
import com.hamric.feature.genres.domain.repository.GenreRepository
import com.hamric.feature.genres.utils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.google.common.truth.Truth.assertThat

@OptIn(ExperimentalCoroutinesApi::class)
class GetGenresUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var useCase: GetGenresUseCase
    private val mockRepository: GenreRepository = mock()

    @Before
    fun setup() {
        useCase = GetGenresUseCase(mockRepository)
    }

    @Test
    fun `invoke should return success with genres when repository returns success`() = runTest {
        val expectedGenres = listOf(
            Genre(id = 28, name = "Action"),
            Genre(id = 35, name = "Comedy")
        )
        whenever(mockRepository.getGenres()).thenReturn(Result.success(expectedGenres))

        val result = useCase()

        assertThat(result.isSuccess).isTrue()
        val genres = result.getOrNull()
        assertThat(genres).isEqualTo(expectedGenres)
    }

    @Test
    fun `invoke should return failure when repository returns failure`() = runTest {
        val exception = RuntimeException("Failed to fetch genres")
        whenever(mockRepository.getGenres()).thenReturn(Result.failure(exception))

        val result = useCase()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `invoke should handle empty list from repository`() = runTest {
        val emptyGenres = emptyList<Genre>()
        whenever(mockRepository.getGenres()).thenReturn(Result.success(emptyGenres))

        val result = useCase()

        assertThat(result.isSuccess).isTrue()
        val genres = result.getOrNull()
        assertThat(genres).isEmpty()
    }
}