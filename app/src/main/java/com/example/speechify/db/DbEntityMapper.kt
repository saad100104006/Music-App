package com.example.speechify.db

interface DbEntityMapper<Model, Entity> {
    fun modelFromEntity(entity: Entity): Model

    fun modelToEntity(model: Model): Entity
}