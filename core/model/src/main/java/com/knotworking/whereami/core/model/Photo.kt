package com.knotworking.whereami.core.model

data class Photo(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val urlM: String?
)
