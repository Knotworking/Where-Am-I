package com.knotworking.whereami.domain.photo.model

data class Photo(
    val id: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val urlM: String?
)
