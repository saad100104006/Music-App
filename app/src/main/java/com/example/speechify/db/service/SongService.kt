package com.example.speechify.db.service

import androidx.lifecycle.LiveData
import com.example.speechify.data.SongModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface SongService {
    fun getSongs(): LiveData<List<SongModel>>

    fun getSongDetails(songId: Int): LiveData<SongModel>

    fun getSongsSingle(): Single<List<SongModel>>

    fun insert(songList: List<SongModel>): Completable

    fun insert(song: SongModel): Completable

    fun nukeTable(): Completable
}