package com.example.musicplayerapp.api

import android.content.Context
import com.example.musicplayerapp.SpotifyAuthManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SpotifyWebAPI(private val context: Context) {

    companion object {
        private const val BASE_URL = "https://api.spotify.com/"
    }

    private val authManager = SpotifyAuthManager(context)

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: SpotifyService = retrofit.create(SpotifyService::class.java)

    fun getAuthHeader(): String? {
        val token = authManager.getAccessToken()
        return token?.let { "Bearer $it" }
    }

    fun isAuthenticated(): Boolean {
        return authManager.isAuthenticated()
    }
}