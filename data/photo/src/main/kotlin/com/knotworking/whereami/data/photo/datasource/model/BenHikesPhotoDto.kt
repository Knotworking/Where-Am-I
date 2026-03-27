package com.knotworking.whereami.data.photo.datasource.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BenHikesPhotoDto(
    @field:Json(name = "url") val url: String,
    @field:Json(name = "filename") val filename: String,
    @field:Json(name = "lat") val lat: Double,
    @field:Json(name = "lng") val lng: Double
)
