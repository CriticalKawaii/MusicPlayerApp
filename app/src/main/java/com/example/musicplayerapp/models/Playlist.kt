package com.example.musicplayerapp.models

data class Playlist(
    val id: String,
    val name: String,
    val uri: String,
    val images: List<Image>,
    val tracks: PlaylistTracks
)