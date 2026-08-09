package com.hamric.core.model

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val createdAt: String,
    val authorDetails: AuthorDetails? = null
)

data class AuthorDetails(
    val name: String,
    val username: String,
    val avatarPath: String? = null,
    val rating: Double? = null
)