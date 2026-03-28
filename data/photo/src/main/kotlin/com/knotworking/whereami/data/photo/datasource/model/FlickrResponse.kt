package com.knotworking.whereami.data.photo.datasource.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlickrResponse(
    @field:Json(name = "photos") val photos: PhotosResponse
)

@JsonClass(generateAdapter = true)
data class PhotosResponse(
    @field:Json(name = "photo") val photo: List<PhotoDto>
)

@JsonClass(generateAdapter = true)
data class PhotoDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "owner") val owner: String,
    @field:Json(name = "secret") val secret: String,
    @field:Json(name = "server") val server: String,
    @field:Json(name = "farm") val farm: Int,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "latitude") val latitude: String,
    @field:Json(name = "longitude") val longitude: String,
    @field:Json(name = "url_m") val urlM: String?
)
