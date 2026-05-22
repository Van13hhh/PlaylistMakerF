package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackSearchResponse>
}