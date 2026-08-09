package com.hamric.core.network.api

import com.hamric.core.network.BuildConfig
import com.hamric.core.network.response.GenresResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): GenresResponse
}