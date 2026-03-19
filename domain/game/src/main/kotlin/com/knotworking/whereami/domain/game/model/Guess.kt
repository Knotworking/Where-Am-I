package com.knotworking.whereami.domain.game.model

data class Guess(
    val latitude: Double,
    val longitude: Double,
    val actualLatitude: Double,
    val actualLongitude: Double,
    val distanceMeters: Double,
    val score: Int
)
