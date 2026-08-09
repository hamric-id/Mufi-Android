package com.hamric.core.network.api

import com.hamric.core.network.BuildConfig
import com.hamric.core.network.response.GenresResponse
import com.hamric.core.network.response.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): GenresResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1
    ): MoviesResponse
}