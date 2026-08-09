package com.hamric.feature.movies.domain.usecase

import androidx.paging.PagingData
import com.hamric.core.model.Movie
import com.hamric.feature.movies.domain.repository.MovieRepository
import com.hamric.feature.movies.utils.CoroutineTestRule
import com.hamric.feature.movies.utils.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.google.common.truth.Truth.assertThat

@OptIn(ExperimentalCoroutinesApi::class)
class GetMoviesByGenreUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var useCase: GetMoviesByGenreUseCase
    private val mockRepository: MovieRepository = mock()
    private val genreId = 28

    @Before
    fun setup() {
        useCase = GetMoviesByGenreUseCase(mockRepository)
    }

    @Test
    fun `invoke should return PagingData from repository`() = runTest {
        val expectedMovies = TestDataFactory.createMovieList(3)
        val pagingData = PagingData.from(expectedMovies)
        whenever(mockRepository.getMoviesByGenre(genreId))
            .thenReturn(flowOf(pagingData))

        val result = useCase(genreId)

        assertThat(result).isNotNull()
        verify(mockRepository).getMoviesByGenre(genreId)
    }

    @Test
    fun `invoke should handle empty result from repository`() = runTest {
        val pagingData = PagingData.from(emptyList<Movie>())
        whenever(mockRepository.getMoviesByGenre(genreId))
            .thenReturn(flowOf(pagingData))

        val result = useCase(genreId)

        assertThat(result).isNotNull()
        verify(mockRepository).getMoviesByGenre(genreId)
    }
}