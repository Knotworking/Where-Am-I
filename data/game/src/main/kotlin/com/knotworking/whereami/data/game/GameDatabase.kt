package com.knotworking.whereami.data.game

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HighScoreEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun highScoreDao(): HighScoreDao
}
