package com.knotworking.whereami.data.photo.datasource.api

import com.knotworking.whereami.data.photo.datasource.model.FlickrResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET("services/rest/")
    suspend fun searchPhotos(
        @Query("method") method: String = "flickr.photos.search",
        @Query("api_key") apiKey: String,
        @Query("has_geo") hasGeo: Int = 1,
        @Query("extras") extras: String = "geo,url_m",
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1,
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1
    ): FlickrResponse
}
