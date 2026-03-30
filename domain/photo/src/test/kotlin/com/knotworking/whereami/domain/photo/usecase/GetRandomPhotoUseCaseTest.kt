package com.knotworking.whereami.domain.photo.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.core.domain.Result
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
        photoRepository.setNextPhoto(mockPhoto)

        val result = getRandomPhotoUseCase()

        assertThat(result).isEqualTo(Result.Success(mockPhoto))
    }

    @Test
    fun `invoke returns error when repository returns no photo`() = runTest {
        photoRepository.setError(DataError.Network.NOT_FOUND)

        val result = getRandomPhotoUseCase()

        assertThat(result).isEqualTo(Result.Error(DataError.Network.NOT_FOUND))
    }
}
