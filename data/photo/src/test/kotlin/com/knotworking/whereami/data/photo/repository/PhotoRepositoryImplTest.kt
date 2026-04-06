package com.knotworking.whereami.data.photo.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.core.domain.Result
import com.knotworking.whereami.data.photo.datasource.BenHikesDataSource
import com.knotworking.whereami.data.photo.datasource.FlickrDataSource
import com.knotworking.whereami.data.photo.datasource.api.BenHikesApi
import com.knotworking.whereami.data.photo.datasource.api.FlickrApi
import com.knotworking.whereami.data.photo.datasource.model.BenHikesPhotoDto
import com.knotworking.whereami.data.photo.datasource.model.FlickrResponse
import com.knotworking.whereami.data.photo.datasource.model.PhotoDto
import com.knotworking.whereami.data.photo.datasource.model.PhotosResponse
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.model.PhotoError
import com.knotworking.whereami.domain.photo.model.PhotoSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import java.io.File

class PhotoRepositoryImplTest {

    private val fakeFlickrApi = FakeFlickrApi()
    private val fakeBenHikesApi = FakeBenHikesApi()

    private fun createRepo(dataStore: DataStore<Preferences> = createTestDataStore()): PhotoRepositoryImpl {
        return PhotoRepositoryImpl(
            FlickrDataSource(fakeFlickrApi, "test-key"),
            BenHikesDataSource(fakeBenHikesApi),
            dataStore
        )
    }

    private fun createTestDataStore(): DataStore<Preferences> {
        val file = File.createTempFile("test_prefs", ".preferences_pb")
        file.delete() // DataStore expects the file to not exist yet
        file.deleteOnExit()
        return PreferenceDataStoreFactory.create { file }
    }

    @Test
    fun `defaults to BENHIKES source`() = runTest {
        val repo = createRepo()

        val source = repo.getPhotoSource().first()

        assertThat(source).isEqualTo(PhotoSource.BENHIKES)
    }

    @Test
    fun `setPhotoSource persists and getPhotoSource returns it`() = runTest {
        val dataStore = createTestDataStore()
        val repo = createRepo(dataStore)

        repo.setPhotoSource(PhotoSource.FLICKR)
        val source = repo.getPhotoSource().first()

        assertThat(source).isEqualTo(PhotoSource.FLICKR)
    }

    @Test
    fun `uses BenHikesDataSource when source is BENHIKES`() = runTest {
        fakeBenHikesApi.response = BenHikesPhotoDto(
            url = "https://example.com/photo.jpg", filename = "hike.jpg", lat = 46.0, lng = 6.0
        )
        val repo = createRepo()

        val result = repo.getRandomGeotaggedPhoto()

        assertThat(result).isInstanceOf<Result.Success<Photo>>()
        assertThat((result as Result.Success).data.id).isEqualTo("hike.jpg")
    }

    @Test
    fun `uses FlickrDataSource when source is FLICKR`() = runTest {
        fakeFlickrApi.response = FlickrResponse(
            PhotosResponse(listOf(
                PhotoDto(
                    id = "flickr123", owner = "o", secret = "s", server = "srv", farm = 1,
                    title = "Flickr Photo", latitude = "48.0", longitude = "2.0", urlM = "url"
                )
            ))
        )
        val dataStore = createTestDataStore()
        val repo = createRepo(dataStore)
        repo.setPhotoSource(PhotoSource.FLICKR)

        val result = repo.getRandomGeotaggedPhoto()

        assertThat(result).isInstanceOf<Result.Success<Photo>>()
        assertThat((result as Result.Success).data.id).isEqualTo("flickr123")
    }

    @Test
    fun `returns NO_PHOTO_FOUND when Flickr returns empty list`() = runTest {
        fakeFlickrApi.response = FlickrResponse(PhotosResponse(photo = emptyList()))
        val dataStore = createTestDataStore()
        val repo = createRepo(dataStore)
        repo.setPhotoSource(PhotoSource.FLICKR)

        val result = repo.getRandomGeotaggedPhoto()

        assertThat(result).isEqualTo(Result.Error(PhotoError.NO_PHOTO_FOUND))
    }

    @Test
    fun `propagates network error as NO_INTERNET`() = runTest {
        fakeBenHikesApi.exception = java.io.IOException("no network")
        val repo = createRepo()

        val result = repo.getRandomGeotaggedPhoto()

        assertThat(result).isInstanceOf<Result.Error<*>>()
        assertThat((result as Result.Error).error).isEqualTo(DataError.Network.NO_INTERNET)
    }
}

private class FakeFlickrApi : FlickrApi {
    var response: FlickrResponse = FlickrResponse(PhotosResponse(photo = emptyList()))
    var exception: Exception? = null

    override suspend fun searchPhotos(
        method: String, apiKey: String, hasGeo: Int, extras: String,
        format: String, noJsonCallback: Int, perPage: Int, page: Int
    ): FlickrResponse {
        exception?.let { throw it }
        return response
    }
}

private class FakeBenHikesApi : BenHikesApi {
    var response = BenHikesPhotoDto(url = "", filename = "", lat = 0.0, lng = 0.0)
    var exception: Exception? = null

    override suspend fun getRandomPhoto(): BenHikesPhotoDto {
        exception?.let { throw it }
        return response
    }
}
