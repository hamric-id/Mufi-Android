package com.hamric.feature.details.utils

import com.hamric.core.model.AuthorDetails
import com.hamric.core.model.Movie
import com.hamric.core.model.Review
import com.hamric.core.model.Video
import com.hamric.core.network.response.AuthorDetailsResponse
import com.hamric.core.network.response.MovieResponse
import com.hamric.core.network.response.ReviewResponse
import com.hamric.core.network.response.ReviewsResponse
import com.hamric.core.network.response.VideoResponse
import com.hamric.core.network.response.VideosResponse

object TestDataFactory {

    fun createMovie(
        id: Int = 123,
        title: String = "Test Movie",
        posterPath: String? = "/test.jpg",
        backdropPath: String? = "/backdrop.jpg",
        overview: String = "Test overview",
        releaseDate: String = "2024-01-01",
        voteAverage: Double = 8.5,
        voteCount: Int = 1000,
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

    fun createReview(
        id: String = "1",
        author: String = "John Doe",
        content: String = "Great movie!",
        createdAt: String = "2024-01-15T10:30:00.000Z",
        authorDetails: AuthorDetails? = createAuthorDetails()
    ): Review {
        return Review(
            id = id,
            author = author,
            content = content,
            createdAt = createdAt,
            authorDetails = authorDetails
        )
    }

    fun createAuthorDetails(
        name: String = "John Doe",
        username: String = "johndoe",
        avatarPath: String? = "/avatar.jpg",
        rating: Double? = 8.5
    ): AuthorDetails {
        return AuthorDetails(
            name = name,
            username = username,
            avatarPath = avatarPath,
            rating = rating
        )
    }

    fun createReviewResponse(
        id: String = "1",
        author: String = "John Doe",
        content: String = "Great movie!",
        createdAt: String = "2024-01-15T10:30:00.000Z",
        authorDetails: AuthorDetailsResponse? = createAuthorDetailsResponse()
    ): ReviewResponse {
        return ReviewResponse(
            id = id,
            author = author,
            content = content,
            createdAt = createdAt,
            authorDetails = authorDetails
        )
    }

    fun createAuthorDetailsResponse(
        name: String = "John Doe",
        username: String = "johndoe",
        avatarPath: String? = "/avatar.jpg",
        rating: Double? = 8.5
    ): AuthorDetailsResponse {
        return AuthorDetailsResponse(
            name = name,
            username = username,
            avatarPath = avatarPath,
            rating = rating
        )
    }

    fun createReviewsResponse(
        page: Int = 1,
        results: List<ReviewResponse> = listOf(createReviewResponse()),
        totalPages: Int = 5,
        totalResults: Int = 100
    ): ReviewsResponse {
        return ReviewsResponse(
            page = page,
            results = results,
            totalPages = totalPages,
            totalResults = totalResults
        )
    }

    fun createReviewList(count: Int = 3): List<Review> {
        return (1..count).map { index ->
            createReview(
                id = index.toString(),
                author = "User $index",
                content = "Review content $index"
            )
        }
    }

    fun createVideo(
        id: String = "1",
        key: String = "abc123",
        name: String = "Official Trailer",
        site: String = "YouTube",
        type: String = "Trailer",
        official: Boolean = true,
        publishedAt: String? = "2024-01-01T00:00:00.000Z"
    ): Video {
        return Video(
            id = id,
            key = key,
            name = name,
            site = site,
            type = type,
            official = official,
            publishedAt = publishedAt
        )
    }

    fun createVideoResponse(
        id: String = "1",
        key: String = "abc123",
        name: String = "Official Trailer",
        site: String = "YouTube",
        type: String = "Trailer",
        official: Boolean = true,
        publishedAt: String? = "2024-01-01T00:00:00.000Z"
    ): VideoResponse {
        return VideoResponse(
            id = id,
            key = key,
            name = name,
            site = site,
            type = type,
            official = official,
            publishedAt = publishedAt
        )
    }
}