package com.example.speechify.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song")
data class SongEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "raw_file_id") val rawFileId: Int,
    @ColumnInfo(name = "image_file_id") val imageFileId: Int,
)
