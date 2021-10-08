package com.example.speechify.db.mapper

import com.example.speechify.data.SongModel
import com.example.speechify.db.DbEntityMapper
import com.example.speechify.db.entity.SongEntity
import javax.inject.Inject

class SongEntityMapper @Inject constructor() : DbEntityMapper<SongModel, SongEntity> {
    override fun modelFromEntity(entity: SongEntity): SongModel {
        return SongModel(
            entity.id, entity.name, entity.description, entity.fileName, entity.rawFileId, entity.imageFileId
        )
    }

    override fun modelToEntity(model: SongModel): SongEntity {
        return SongEntity(
            model.id, model.title, model.details, model.fileName, model.rawFileId, model.imageFile
        )
    }
}