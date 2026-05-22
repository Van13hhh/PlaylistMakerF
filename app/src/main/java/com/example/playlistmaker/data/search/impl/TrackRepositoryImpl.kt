package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.example.playlistmaker.data.search.dto.TrackSearchResponse
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.search.model.TrackSearchResult
import com.example.playlistmaker.domain.search.TrackRepository

class TrackRepositoryImpl(private val networkClient: NetworkClient): TrackRepository {
    override fun searchTrack(expression: String): TrackSearchResult {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        return if (response.resultCode == 200) {
            val tracks = (response as TrackSearchResponse).results.map {
                Track(
                    it.trackName, it.artistName, it.trackTimeMillis, it.artworkUrl100,
                    it.trackId, it.collectionName, it.releaseDate, it.primaryGenreName,
                    it.country, it.previewUrl
                )
            }
            TrackSearchResult(tracks, 200)
        } else {
            // Возвращаем пустой список и код ошибки (например, -1)
            TrackSearchResult(emptyList(), response.resultCode)
        }
    }
}