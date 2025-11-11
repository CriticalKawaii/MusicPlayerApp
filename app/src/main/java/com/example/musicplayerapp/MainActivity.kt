package com.example.musicplayerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musicplayerapp.api.SpotifyWebAPI
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var authManager: SpotifyAuthManager
    private lateinit var remoteManager: SpotifyRemoteManager
    private lateinit var webAPI: SpotifyWebAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = SpotifyAuthManager(this)
        remoteManager = SpotifyRemoteManager(this)
        webAPI = SpotifyWebAPI(this)

        setupUI()

        if (!remoteManager.isSpotifyInstalled()) {
            Toast.makeText(
                this,
                "Spotify app is not installed",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupUI() {
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            authManager.authenticateForWebAPI(this)
        }

        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            connectToSpotify()
        }

        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            searchTracks("Bohemian Rhapsody")
        }

        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            remoteManager.play("spotify:track:3z8h0TU7ReDPLIbEnYhWZb")
        }

        findViewById<Button>(R.id.btnPause).setOnClickListener {
            remoteManager.pause()
        }
    }

    private fun connectToSpotify() {
        remoteManager.connect(object : SpotifyRemoteManager.ConnectionListener {
            override fun onConnected() {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Connected to Spotify!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onConnectionFailed(error: Throwable) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Connection failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun searchTracks(query: String) {
        if (!webAPI.isAuthenticated()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val authHeader = webAPI.getAuthHeader() ?: return@launch
                val response = webAPI.service.search(
                    auth = authHeader,
                    query = query,
                    type = "track"
                )

                if (response.isSuccessful) {
                    val tracks = response.body()?.tracks?.items
                    tracks?.forEach { track ->
                        Log.d(TAG, "Found: ${track.name} by ${track.artists[0].name}")
                    }

                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Found ${tracks?.size} tracks",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e(TAG, "Search failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Search error", e)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val success = authManager.handleAuthResponse(requestCode, resultCode, data)
        if (success) {
            Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        remoteManager.disconnect()
    }
}