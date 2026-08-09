package com.hamric.core.network.response

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("author_details")
    val authorDetails: AuthorDetailsResponse? = null
)

data class AuthorDetailsResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("avatar_path")
    val avatarPath: String? = null,
    @SerializedName("rating")
    val rating: Double? = null
)

data class ReviewsResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<ReviewResponse>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)