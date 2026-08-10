package com.hamric.core.network.response

import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("site")
    val site: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("official")
    val official: Boolean = false,
    @SerializedName("published_at")
    val publishedAt: String? = null
)

data class VideosResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<VideoResponse>
)