package com.knotworking.whereami.data.photo.datasource

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.knotworking.whereami.data.photo.datasource.api.FlickrApi
import com.knotworking.whereami.data.photo.datasource.model.FlickrResponse
import com.knotworking.whereami.data.photo.datasource.model.PhotoDto
import com.knotworking.whereami.data.photo.datasource.model.PhotosResponse
import com.knotworking.whereami.domain.photo.model.Photo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FlickrDataSourceTest {

    private val fakeApi = FakeFlickrApi()
    private val dataSource = FlickrDataSource(fakeApi, "test-key")

    @Test
    fun `maps DTO to Photo correctly`() = runTest {
        fakeApi.response = flickrResponse(
            PhotoDto(
                id = "123", owner = "owner", secret = "s", server = "srv", farm = 1,
                title = "Sunset", latitude = "48.8566", longitude = "2.3522", urlM = "https://img.jpg"
            )
        )

        val photo = dataSource.fetchPhoto()

        assertThat(photo).isEqualTo(
            Photo(id = "123", title = "Sunset", latitude = 48.8566, longitude = 2.3522, urlM = "https://img.jpg")
        )
    }

    @Test
    fun `handles null urlM`() = runTest {
        fakeApi.response = flickrResponse(
            PhotoDto(
                id = "1", owner = "o", secret = "s", server = "srv", farm = 1,
                title = "T", latitude = "10.0", longitude = "20.0", urlM = null
            )
        )

        val photo = dataSource.fetchPhoto()

        assertThat(photo?.urlM).isNull()
    }

    @Test
    fun `invalid latitude falls back to 0`() = runTest {
        fakeApi.response = flickrResponse(
            PhotoDto(
                id = "1", owner = "o", secret = "s", server = "srv", farm = 1,
                title = "T", latitude = "not_a_number", longitude = "20.0", urlM = null
            )
        )

        val photo = dataSource.fetchPhoto()

        assertThat(photo?.latitude).isEqualTo(0.0)
    }

    @Test
    fun `invalid longitude falls back to 0`() = runTest {
        fakeApi.response = flickrResponse(
            PhotoDto(
                id = "1", owner = "o", secret = "s", server = "srv", farm = 1,
                title = "T", latitude = "10.0", longitude = "abc", urlM = null
            )
        )

        val photo = dataSource.fetchPhoto()

        assertThat(photo?.longitude).isEqualTo(0.0)
    }

    @Test
    fun `returns null when photo list is empty`() = runTest {
        fakeApi.response = FlickrResponse(PhotosResponse(photo = emptyList()))

        val photo = dataSource.fetchPhoto()

        assertThat(photo).isNull()
    }

    private fun flickrResponse(vararg dtos: PhotoDto) =
        FlickrResponse(PhotosResponse(photo = dtos.toList()))
}

private class FakeFlickrApi : FlickrApi {
    var response: FlickrResponse = FlickrResponse(PhotosResponse(photo = emptyList()))

    override suspend fun searchPhotos(
        method: String, apiKey: String, hasGeo: Int, extras: String,
        format: String, noJsonCallback: Int, perPage: Int, page: Int
    ): FlickrResponse = response
}
