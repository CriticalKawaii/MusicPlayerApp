package com.example.musicplayerapp.models

data class PlaylistSimplified(
    val id: String,
    val name: String,
    val uri: String,
    val images: List<Image>,
    val tracks: PlaylistTracksInfo
)