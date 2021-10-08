package com.example.speechify.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.speechify.db.entity.SongEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getSongs(): LiveData<List<SongEntity>>

    @Query("SELECT * FROM song WHERE id=:songId")
    fun getSongDetails(songId: Int): LiveData<SongEntity>

    @Query("SELECT * FROM song")
    fun getSongsSingle(): Single<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(songEntityList: List<SongEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(songEntity: SongEntity): Completable

    @Query("DELETE FROM song")
    fun nukeTable(): Completable
}