package com.knotworking.whereami.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlickrResponse(
    @Json(name = "photos") val photos: PhotosResponse
)

@JsonClass(generateAdapter = true)
data class PhotosResponse(
    @Json(name = "photo") val photo: List<PhotoDto>
)

@JsonClass(generateAdapter = true)
data class PhotoDto(
    @Json(name = "id") val id: String,
    @Json(name = "owner") val owner: String,
    @Json(name = "secret") val secret: String,
    @Json(name = "server") val server: String,
    @Json(name = "farm") val farm: Int,
    @Json(name = "title") val title: String,
    @Json(name = "latitude") val latitude: String,
    @Json(name = "longitude") val longitude: String,
    @Json(name = "url_m") val urlM: String?
)
