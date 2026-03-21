package com.knotworking.whereami.domain.photo.usecase

import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import com.knotworking.whereami.domain.photo.model.Photo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRandomPhotoUseCaseTest {

    private val photoRepository: PhotoRepository = mockk()
    private val getRandomPhotoUseCase = GetRandomPhotoUseCase(photoRepository)

    @Test
    fun `invoke returns photo from repository`() = runBlocking {
        val mockPhoto = Photo("1", "o", 0.0, 0.0, "t")
        coEvery { photoRepository.getRandomGeotaggedPhoto() } returns mockPhoto

        val result = getRandomPhotoUseCase()

        assertEquals(mockPhoto, result)
    }

    @Test
    fun `invoke returns null when repository returns no photo`() = runBlocking {
        coEvery { photoRepository.getRandomGeotaggedPhoto() } returns null

        val result = getRandomPhotoUseCase()

        assertEquals(null, result)
    }
}
