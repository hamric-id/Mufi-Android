package com.hamric.feature.details.domain.usecase

import androidx.paging.PagingData
import com.hamric.core.model.Review
import com.hamric.feature.details.domain.repository.MovieDetailRepository
import com.hamric.feature.details.utils.CoroutineTestRule
import com.hamric.feature.details.utils.TestDataFactory
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
class GetMovieReviewsUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var useCase: GetMovieReviewsUseCase
    private val mockRepository: MovieDetailRepository = mock()
    private val movieId = 123

    @Before
    fun setup() {
        useCase = GetMovieReviewsUseCase(mockRepository)
    }

    @Test
    fun `invoke should return PagingData from repository`() = runTest {
        val expectedReviews = TestDataFactory.createReviewList(3)
        val pagingData = PagingData.from(expectedReviews)
        whenever(mockRepository.getMovieReviews(movieId)).thenReturn(flowOf(pagingData))

        val result = useCase(movieId)

        assertThat(result).isNotNull()
        verify(mockRepository).getMovieReviews(movieId)
    }

    @Test
    fun `invoke should handle empty reviews`() = runTest {
        val pagingData = PagingData.from(emptyList<Review>())
        whenever(mockRepository.getMovieReviews(movieId)).thenReturn(flowOf(pagingData))

        val result = useCase(movieId)

        assertThat(result).isNotNull()
        verify(mockRepository).getMovieReviews(movieId)
    }
}