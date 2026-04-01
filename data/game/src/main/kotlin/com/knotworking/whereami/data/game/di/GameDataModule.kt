package com.knotworking.whereami.data.game.di

import android.content.Context
import androidx.room.Room
import com.knotworking.whereami.data.game.GameDatabase
import com.knotworking.whereami.data.game.HighScoreDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GameDataModule {

    @Provides
    @Singleton
    fun provideGameDatabase(@ApplicationContext context: Context): GameDatabase =
        Room.databaseBuilder(context, GameDatabase::class.java, "game_database")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    @Singleton
    fun provideHighScoreDao(database: GameDatabase): HighScoreDao = database.highScoreDao()
}
