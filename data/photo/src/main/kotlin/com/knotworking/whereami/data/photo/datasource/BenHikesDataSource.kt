package com.knotworking.whereami.data.photo.datasource

import android.util.Log
import com.knotworking.whereami.data.photo.datasource.api.BenHikesApi
import com.knotworking.whereami.domain.photo.model.Photo
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BenHikesDataSource @Inject constructor(
    private val benHikesApi: BenHikesApi
) : RemotePhotoDataSource {

    override suspend fun fetchPhotos(count: Int): List<Photo> {
        val photos = mutableListOf<Photo>()
        repeat(count) {
            try {
                val dto = benHikesApi.getRandomPhoto()
                photos.add(
                    Photo(
                        id = dto.filename,
                        title = dto.filename,
                        latitude = dto.lat,
                        longitude = dto.lng,
                        urlM = dto.url
                    )
                )
            } catch (e: IOException) {
                Log.w(TAG, "Network error fetching photo", e)
            } catch (e: HttpException) {
                Log.w(TAG, "HTTP ${e.code()} error fetching photo", e)
            }
        }
        return photos
    }

    companion object {
        private const val TAG = "BenHikesDataSource"
    }
}
