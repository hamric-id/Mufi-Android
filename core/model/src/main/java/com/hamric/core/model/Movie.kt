package com.hamric.core.model

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val genreIds: List<Int> = emptyList(),
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val popularity: Double = 0.0,
    val adult: Boolean = false,
    val video: Boolean = false
)