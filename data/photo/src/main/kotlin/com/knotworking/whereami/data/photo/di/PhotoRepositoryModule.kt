package com.knotworking.whereami.data.photo.di

import com.knotworking.whereami.data.photo.repository.PhotoRepositoryImpl
import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PhotoRepositoryModule {

    @Binds
    @Singleton
    fun bindPhotoRepository(
        photoRepositoryImpl: PhotoRepositoryImpl
    ): PhotoRepository
}
