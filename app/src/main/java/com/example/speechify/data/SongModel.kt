package com.example.speechify.data

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

data class SongModel(
    val id: Int,
    val title: String,
    val details: String,
    val fileName: String,
    @RawRes val rawFileId: Int,
    @DrawableRes val imageFile: Int
)