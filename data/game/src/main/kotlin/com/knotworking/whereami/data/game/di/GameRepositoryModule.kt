package com.knotworking.whereami.data.game.di

import com.knotworking.whereami.data.game.RoomHighScoreRepository
import com.knotworking.whereami.domain.game.repository.HighScoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface GameRepositoryModule {

    @Binds
    @Singleton
    fun bindHighScoreRepository(
        roomHighScoreRepository: RoomHighScoreRepository
    ): HighScoreRepository
}
