package com.knotworking.whereami.core.network

import com.knotworking.whereami.core.network.model.BenHikesPhotoDto
import retrofit2.http.GET

interface BenHikesApi {
    @GET("api/random-photo.php")
    suspend fun getRandomPhoto(): BenHikesPhotoDto
}
