package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {
    private val imdBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdService = retrofit.create(IMDApiService::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto !is TrackSearchRequest) return Response().apply { resultCode = 400 }

        return try {
            val resp = imdService.search(dto.expression).execute()
            val body = resp.body() ?: Response()
            body.apply { resultCode = resp.code() }
        } catch (e: Exception) {
            // Ловим отсутствие интернета и возвращаем -1
            Response().apply { resultCode = -1 }
        }
    }
}