package com.hamric.feature.genres.data.repository

import com.hamric.core.model.Genre
import com.hamric.core.network.api.TmdbApi
import com.hamric.core.network.mapper.toDomainModels
import com.hamric.feature.genres.domain.repository.GenreRepository
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : GenreRepository {

    override suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val response = api.getGenres()

            if (response == null) {
                return Result.success(emptyList())
            }

            val genres = response.genres?.toDomainModels() ?: emptyList()

            val validGenres = genres.filter { genre ->
                genre.id > 0 && genre.name.isNotBlank()
            }

            Result.success(validGenres)

        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Connection timeout. Please try again."))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Please check your internet."))
        } catch (e: Exception) {
            Result.failure(Exception("Unable to load genres. Please try again."))
        }
    }
}