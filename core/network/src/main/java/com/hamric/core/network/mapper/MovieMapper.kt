package com.hamric.core.network.mapper

import com.hamric.core.model.Movie
import com.hamric.core.network.response.MovieResponse

fun MovieResponse.toDomainModel(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genreIds = genreIds?: emptyList(),
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        popularity = popularity,
        adult = adult,
        video = video
    )
}
