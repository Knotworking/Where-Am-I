package com.knotworking.whereami.data.photo.datasource

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.knotworking.whereami.data.photo.datasource.api.BenHikesApi
import com.knotworking.whereami.data.photo.datasource.model.BenHikesPhotoDto
import com.knotworking.whereami.domain.photo.model.Photo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class BenHikesDataSourceTest {

    private val fakeApi = FakeBenHikesApi()
    private val dataSource = BenHikesDataSource(fakeApi)

    @Test
    fun `maps DTO to Photo correctly`() = runTest {
        fakeApi.response = BenHikesPhotoDto(
            url = "https://example.com/photo.jpg",
            filename = "mountain.jpg",
            lat = 46.5197,
            lng = 6.6323
        )

        val photo = dataSource.fetchPhoto()

        assertThat(photo).isEqualTo(
            Photo(
                id = "mountain.jpg",
                title = "mountain.jpg",
                latitude = 46.5197,
                longitude = 6.6323,
                urlM = "https://example.com/photo.jpg"
            )
        )
    }
}

private class FakeBenHikesApi : BenHikesApi {
    var response = BenHikesPhotoDto(url = "", filename = "", lat = 0.0, lng = 0.0)

    override suspend fun getRandomPhoto(): BenHikesPhotoDto = response
}
