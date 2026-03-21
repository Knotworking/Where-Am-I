package com.knotworking.whereami.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BenHikesPhotoDto(
    @Json(name = "url") val url: String,
    @Json(name = "filename") val filename: String,
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double
)
