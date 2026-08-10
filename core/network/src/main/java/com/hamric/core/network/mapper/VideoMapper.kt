package com.hamric.core.network.mapper

import com.hamric.core.model.Video
import com.hamric.core.network.response.VideoResponse

fun VideoResponse.toDomainModel(): Video {
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


fun List<VideoResponse>.getTrailer(): Video? {
    return (this
        .filter { it.site.equals("YouTube", ignoreCase = true) }
        .firstOrNull { it.type.equals("Trailer", ignoreCase = true) }
        ?: this.firstOrNull { it.site.equals("YouTube", ignoreCase = true) })?.toDomainModel()
}