package com.example.musicplayerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse


class LoginActivity : AppCompatActivity() {
    private val REQUEST_CODE = 1337
    private val REDIRECT_URI = "https://com.example.musicplayerapp/callback"
    private val clientId = "8a35e6c1d45243d183d763ad86f85771"

    var builder = AuthorizationRequest.Builder(clientId,
        AuthorizationResponse.Type.CODE, REDIRECT_URI)


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)

            when (response.getType()) {
                AuthorizationResponse.Type.CODE -> {}
                AuthorizationResponse.Type.ERROR -> {}
                else -> {}
            }
        }
    }
}