package com.example.musicplayerapp.api

import com.example.musicplayerapp.models.*
import retrofit2.Response
import retrofit2.http.*

interface SpotifyService {

    @GET("v1/search")
    suspend fun search(
        @Header("Authorization") auth: String,
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("limit") limit: Int = 20
    ): Response<SearchResult>

    @GET("v1/me/playlists")
    suspend fun getUserPlaylists(
        @Header("Authorization") auth: String,
        @Query("limit") limit: Int = 50
    ): Response<PlaylistsResponse>

    @GET("v1/playlists/{playlist_id}")
    suspend fun getPlaylist(
        @Header("Authorization") auth: String,
        @Path("playlist_id") playlistId: String
    ): Response<Playlist>

    @GET("v1/me/tracks")
    suspend fun getSavedTracks(
        @Header("Authorization") auth: String,
        @Query("limit") limit: Int = 50
    ): Response<SavedTracksResponse>

    @GET("v1/me/top/tracks")
    suspend fun getTopTracks(
        @Header("Authorization") auth: String,
        @Query("limit") limit: Int = 20,
        @Query("time_range") timeRange: String = "medium_term"
    ): Response<TopTracksResponse>

    @GET("v1/albums/{album_id}")
    suspend fun getAlbum(
        @Header("Authorization") auth: String,
        @Path("album_id") albumId: String
    ): Response<Album>
}