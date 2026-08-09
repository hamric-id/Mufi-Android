package com.hamric.core.network.response

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null,
    @SerializedName("original_language")
    val originalLanguage: String? = null,
    @SerializedName("original_title")
    val originalTitle: String? = null,
    @SerializedName("popularity")
    val popularity: Double = 0.0,
    @SerializedName("adult")
    val adult: Boolean = false,
    @SerializedName("video")
    val video: Boolean = false
)

data class MoviesResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<MovieResponse>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)