package com.hamric.feature.genres.data.repository

import com.hamric.core.network.api.TmdbApi
import com.hamric.core.network.response.GenreResponse
import com.hamric.core.network.response.GenresResponse
import com.hamric.feature.genres.utils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GenreRepositoryImplTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var repository: GenreRepositoryImpl
    private val mockApi: TmdbApi = mock()

    @Before
    fun setup() {
        repository = GenreRepositoryImpl(mockApi)
    }

    @Test
    fun `getGenres should return success with list of genres when API call is successful`() = runTest {
        val mockGenreResponse = listOf(
            GenreResponse(id = 28, name = "Action"),
            GenreResponse(id = 35, name = "Comedy")
        )
        val mockGenresResponse = GenresResponse(mockGenreResponse)

        whenever(mockApi.getGenres()).thenReturn(mockGenresResponse)

        val result = repository.getGenres()

        assertThat(result.isSuccess).isTrue()
        val genres = result.getOrNull()
        assertThat(genres).isNotNull()
        assertThat(genres).hasSize(2)
        assertThat(genres?.get(0)?.id).isEqualTo(28)
        assertThat(genres?.get(0)?.name).isEqualTo("Action")
        assertThat(genres?.get(1)?.id).isEqualTo(35)
        assertThat(genres?.get(1)?.name).isEqualTo("Comedy")
    }

    @Test
    fun `getGenres should return failure when API call throws exception`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(mockApi.getGenres()).thenThrow(exception)

        val result = repository.getGenres()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isNotNull()
    }

    @Test
    fun `getGenres should handle empty list from API`() = runTest {
        val mockGenresResponse = GenresResponse(emptyList())
        whenever(mockApi.getGenres()).thenReturn(mockGenresResponse)

        val result = repository.getGenres()

        assertThat(result.isSuccess).isTrue()
        val genres = result.getOrNull()
        assertThat(genres).isNotNull()
        assertThat(genres).isEmpty()
    }
}
