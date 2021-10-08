package com.example.speechify.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.speechify.db.dao.SongDao
import com.example.speechify.db.entity.SongEntity

@Database(
    entities = [
        SongEntity::class,
    ], version = 1
)
abstract class SpeechifyDatabase : RoomDatabase() {

    abstract fun getSongDao(): SongDao

    companion object {
        const val DATABASE_NAME = "speechify_database"
    }
}