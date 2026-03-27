package com.knotworking.whereami.domain.photo.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.knotworking.whereami.domain.photo.FakePhotoRepository
import com.knotworking.whereami.domain.photo.model.Photo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GetRandomPhotoUseCaseTest {

    private val photoRepository = FakePhotoRepository()
    private val getRandomPhotoUseCase = GetRandomPhotoUseCase(photoRepository)

    @Test
    fun `invoke returns photo from repository`() = runTest {
        val mockPhoto = Photo("1", "o", 0.0, 0.0, "t")
        photoRepository.nextPhoto = mockPhoto

        val result = getRandomPhotoUseCase()

        assertThat(result).isEqualTo(mockPhoto)
    }

    @Test
    fun `invoke returns null when repository returns no photo`() = runTest {
        photoRepository.nextPhoto = null

        val result = getRandomPhotoUseCase()

        assertThat(result).isNull()
    }
}
