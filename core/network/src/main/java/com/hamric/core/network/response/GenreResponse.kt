package com.hamric.core.network.response

import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class GenresResponse(
    @SerializedName("genres")
    val genres: List<GenreResponse>
)