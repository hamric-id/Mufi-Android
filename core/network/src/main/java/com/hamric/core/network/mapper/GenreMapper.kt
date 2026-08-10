package com.hamric.core.network.mapper

import com.hamric.core.model.Genre
import com.hamric.core.network.response.GenreResponse

fun GenreResponse.toDomainModel(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun List<GenreResponse>.toDomainModels(): List<Genre> {
    return this.map { it.toDomainModel() }
}