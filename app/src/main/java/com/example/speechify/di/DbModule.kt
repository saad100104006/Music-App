package com.example.speechify.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.speechify.db.SpeechifyDatabase
import com.example.speechify.db.dao.SongDao
import com.example.speechify.db.mapper.SongEntityMapper
import com.example.speechify.db.service.SongService
import com.example.speechify.db.service.SongServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Singleton
    @Provides
    fun provideSpeechifyDatabase(@ApplicationContext context: Context): SpeechifyDatabase {
        return Room
            .databaseBuilder(
                context,
                SpeechifyDatabase::class.java,
                SpeechifyDatabase.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun getSongDao(mdsDatabase: SpeechifyDatabase): SongDao {
        return mdsDatabase.getSongDao()
    }


    @Singleton
    @Provides
    fun getSongService(
        songDao: SongDao,
        songEntityMapper: SongEntityMapper
    ): SongService {
        return SongServiceImpl(songDao, songEntityMapper)
    }

}