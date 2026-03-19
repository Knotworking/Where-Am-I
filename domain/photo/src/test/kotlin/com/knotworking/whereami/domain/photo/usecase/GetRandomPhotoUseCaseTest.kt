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
        val mockPhotos = listOf(
            Photo("1", "o", "s", "s", 1, "t", 0.0, 0.0, "u")
        )
        coEvery { photoRepository.getRandomGeotaggedPhotos(10) } returns mockPhotos

        val result = getRandomPhotoUseCase()

        assertEquals(mockPhotos[0], result)
    }

    @Test
    fun `invoke returns null when repository returns empty list`() = runBlocking {
        coEvery { photoRepository.getRandomGeotaggedPhotos(10) } returns emptyList()

        val result = getRandomPhotoUseCase()

        assertEquals(null, result)
    }
}
