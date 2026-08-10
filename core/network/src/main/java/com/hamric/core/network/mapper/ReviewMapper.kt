package com.hamric.core.network.mapper

import com.hamric.core.model.AuthorDetails
import com.hamric.core.model.Review
import com.hamric.core.network.response.AuthorDetailsResponse
import com.hamric.core.network.response.ReviewResponse

fun ReviewResponse.toDomainModel(): Review {
    return Review(
        id = id,
        author = author,
        content = content,
        createdAt = createdAt,
        authorDetails = authorDetails?.toDomainModel()
    )
}

fun AuthorDetailsResponse.toDomainModel(): AuthorDetails {
    return AuthorDetails(
        name = name,
        username = username,
        avatarPath = avatarPath,
        rating = rating
    )
}

fun List<ReviewResponse>.toDomainModels(): List<Review> {
    return this.map { it.toDomainModel() }
}