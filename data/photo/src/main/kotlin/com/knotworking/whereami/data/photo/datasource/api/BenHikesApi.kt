package com.knotworking.whereami.data.photo.datasource.api

import com.knotworking.whereami.data.photo.datasource.model.BenHikesPhotoDto
import retrofit2.http.GET

interface BenHikesApi {
    @GET("api/random-photo.php")
    suspend fun getRandomPhoto(): BenHikesPhotoDto
}
