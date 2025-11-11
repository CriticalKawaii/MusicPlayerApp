package com.example.musicplayerapp

import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track

class SpotifyRemoteManager(private val context: Context) {

    companion object {
        private const val TAG = "SpotifyRemoteManager"
        private const val CLIENT_ID = "8a35e6c1d45243d183d763ad86f85771"
        private const val REDIRECT_URI = "myapp://callback"
    }

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var connectionListener: ConnectionListener? = null

    interface ConnectionListener {
        fun onConnected()
        fun onConnectionFailed(error: Throwable)
    }

    fun connect(listener: ConnectionListener) {
        this.connectionListener = listener

        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)  // Show auth if not logged in
            .build()

        SpotifyAppRemote.connect(
            context,
            connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    Log.d(TAG, "Connected to Spotify App Remote")
                    listener.onConnected()
                    subscribeToPlayerState()
                }

                override fun onFailure(error: Throwable) {
                    Log.e(TAG, "Failed to connect", error)
                    listener.onConnectionFailed(error)
                }
            }
        )
    }

    fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            spotifyAppRemote = null
        }
    }

    private fun subscribeToPlayerState() {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
            val track = playerState.track
            Log.d(TAG, "Now playing: ${track.name} by ${track.artist.name}")
            // You can update your UI here
        }
    }

    fun play(uri: String) {
        spotifyAppRemote?.playerApi?.play(uri)
    }

    fun resume() {
        spotifyAppRemote?.playerApi?.resume()
    }

    fun pause() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun skipNext() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    fun skipPrevious() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }

    fun seekTo(positionMs: Long) {
        spotifyAppRemote?.playerApi?.seekTo(positionMs)
    }

    fun getPlayerState(callback: (PlayerState) -> Unit) {
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { playerState ->
            callback(playerState)
        }
    }

    fun isSpotifyInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.spotify.music", 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isConnected(): Boolean = spotifyAppRemote != null
}