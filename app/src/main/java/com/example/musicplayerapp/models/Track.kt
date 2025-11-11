package com.example.musicplayerapp.models

import com.google.gson.annotations.SerializedName

data class Track(
    val id: String,
    val name: String,
    val uri: String,
    val artists: List<Artist>,
    val album: Album,
    @SerializedName("duration_ms") val durationMs: Int,
    @SerializedName("preview_url") val previewUrl: String?
)

