package com.hamric.feature.movies.utils

import com.hamric.core.model.Movie
import com.hamric.core.network.response.MovieResponse
import com.hamric.core.network.response.MoviesResponse

object TestDataFactory {

    fun createMovie(
        id: Int = 1,
        title: String = "Test Movie",
        posterPath: String? = "/test.jpg",
        backdropPath: String? = "/backdrop.jpg",
        overview: String = "Test overview",
        releaseDate: String = "2024-01-01",
        voteAverage: Double = 7.5,
        voteCount: Int = 100,
        genreIds: List<Int> = listOf(28, 12)
    ): Movie {
        return Movie(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            overview = overview,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            genreIds = genreIds
        )
    }

    fun createMovieResponse(
        id: Int = 1,
        title: String = "Test Movie",
        posterPath: String? = "/test.jpg",
        backdropPath: String? = "/backdrop.jpg",
        overview: String = "Test overview",
        releaseDate: String = "2024-01-01",
        voteAverage: Double = 7.5,
        voteCount: Int = 100,
        genreIds: List<Int> = listOf(28, 12)
    ): MovieResponse {
        return MovieResponse(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            overview = overview,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            genreIds = genreIds
        )
    }

    fun createMoviesResponse(
        page: Int = 1,
        results: List<MovieResponse> = listOf(createMovieResponse()),
        totalPages: Int = 5,
        totalResults: Int = 100
    ): MoviesResponse {
        return MoviesResponse(
            page = page,
            results = results,
            totalPages = totalPages,
            totalResults = totalResults
        )
    }

    fun createMovieList(count: Int = 3): List<Movie> {
        return (1..count).map { index ->
            createMovie(
                id = index,
                title = "Test Movie $index",
                voteAverage = 7.0 + index
            )
        }
    }
}