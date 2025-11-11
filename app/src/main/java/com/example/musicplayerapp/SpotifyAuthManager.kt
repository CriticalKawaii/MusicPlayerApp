package com.example.musicplayerapp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import androidx.core.content.edit

class SpotifyAuthManager(private val context: Context) {
    companion object {
        private const val TAG = "SpotifyAuthManager"
        const val AUTH_TOKEN_REQUEST_CODE = 0x10
        const val AUTH_CODE_REQUEST_CODE = 0x11

        private const val CLIENT_ID = "8a35e6c1d45243d183d763ad86f85771"
        private const val REDIRECT_URI = "myapp://callback"

        private const val PREFS_NAME = "SpotifyPrefs"
        private const val KEY_ACCESS_TOKEN  = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_EXPIRES_AT = "expires_at"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun authenticateForAppRemote(activity: Activity){
        val request = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            REDIRECT_URI
        ).setScopes(arrayOf("app-remote-control",
            "user-modify-playback-state",
            "user-read-playback-state")).build()

        AuthorizationClient.openLoginActivity(
            activity,
            AUTH_TOKEN_REQUEST_CODE,
            request
        )
    }

    fun authenticateForWebAPI(activity: Activity) {
        val request = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.CODE,
            REDIRECT_URI
        )
            .setScopes(arrayOf(
                "user-read-private",
                "user-read-email",
                "playlist-read-private",
                "playlist-read-collaborative",
                "user-library-read",
                "user-top-read",
                "user-read-recently-played",
                "app-remote-control",
                "user-modify-playback-state",
                "user-read-playback-state"
            ))
            .setShowDialog(true)
            .build()

        AuthorizationClient.openLoginActivity(
            activity,
            AUTH_CODE_REQUEST_CODE,
            request
        )
    }

    fun handleAuthResponse(requestCode: Int, resultCode: Int, intent: android.content.Intent?): Boolean {
        if (requestCode == AUTH_TOKEN_REQUEST_CODE || requestCode == AUTH_CODE_REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)

            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    saveAccessToken(
                        response.accessToken,
                        response.expiresIn
                    )
                    Log.d(TAG, "Authentication successful!")
                    return true
                }

                AuthorizationResponse.Type.CODE -> {
                    Log.d(TAG, "Got auth code: ${response.code}")
                    return true
                }

                AuthorizationResponse.Type.ERROR -> {
                    Log.e(TAG, "Auth error: ${response.error}")
                    return false
                }

                else -> {
                    Log.e(TAG, "Auth cancelled or unknown response")
                    return false
                }
            }
        }
        return false
    }

    private fun saveAccessToken(token: String, expiresIn: Int) {
        val expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, token)
                .putLong(KEY_EXPIRES_AT, expiresAt)
        }
    }

    fun getAccessToken(): String? {
        val token = prefs.getString(KEY_ACCESS_TOKEN, null)
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0)

        if (System.currentTimeMillis() >= expiresAt) {
            return null
        }

        return token
    }

    fun isAuthenticated(): Boolean {
        return getAccessToken() != null
    }

    fun clearTokens() {
        prefs.edit { clear() }
    }
}