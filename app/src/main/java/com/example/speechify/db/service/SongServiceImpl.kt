package com.example.speechify.db.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.speechify.data.SongModel
import com.example.speechify.db.dao.SongDao
import com.example.speechify.db.mapper.SongEntityMapper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SongServiceImpl @Inject constructor(
    private val songDao: SongDao,
    private val songEntityMapper: SongEntityMapper
) : SongService {
    override fun getSongs(): LiveData<List<SongModel>> {
        return songDao.getSongs()
            .map { list ->
                list.map {
                    songEntityMapper.modelFromEntity(it)
                }
            }
    }

    override fun getSongDetails(songId: Int): LiveData<SongModel> {
        return songDao.getSongDetails(songId)
            .map {
                songEntityMapper.modelFromEntity(it)
            }
    }

    override fun getSongsSingle(): Single<List<SongModel>> {
        return songDao.getSongsSingle().map { list ->
            list.map {
                songEntityMapper.modelFromEntity(it)
            }
        }
            .subscribeOn(Schedulers.io())
    }

    override fun insert(songList: List<SongModel>): Completable {
        val songEntityList = songList.map {
            songEntityMapper.modelToEntity(it)
        }

        return songDao.insert(songEntityList)
            .subscribeOn(Schedulers.io())

    }


    override fun insert(song: SongModel): Completable {
        val songEntity = songEntityMapper.modelToEntity(song)
        return songDao.insert(songEntity)
            .subscribeOn(Schedulers.io())

    }

    override fun nukeTable(): Completable {
        return songDao.nukeTable()
            .subscribeOn(Schedulers.io())

    }

}