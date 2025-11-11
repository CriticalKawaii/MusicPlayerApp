package com.example.musicplayerapp.models

import com.google.gson.annotations.SerializedName

data class Album(
    val id: String,
    val name: String,
    val uri: String,
    val images: List<Image>,
    @SerializedName("release_date") val releaseDate: String?
)