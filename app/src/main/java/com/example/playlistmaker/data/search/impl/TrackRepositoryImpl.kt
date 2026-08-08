package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.example.playlistmaker.data.search.dto.TrackSearchResponse
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.search.model.TrackSearchResult
import com.example.playlistmaker.domain.search.TrackRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: RetrofitNetworkClient,
    private val appDatabase: AppDatabase
) : TrackRepository {
    override fun searchTrack(expression: String): Flow<Resource<TrackSearchResult>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

         if (response.resultCode == 200) {
            val tracks = (response as TrackSearchResponse).results.map {
                val isFavourite = appDatabase.trackDao().getFavouriteTracksId()?.contains(it.trackId) ?: false
                Track(
                    it.trackName, it.artistName, it.trackTimeMillis, it.artworkUrl100,
                    it.trackId, it.collectionName, it.releaseDate, it.primaryGenreName,
                    it.country, it.previewUrl, isFavourite
                )
            }
            emit(Resource.Success(TrackSearchResult(tracks, 200)))
        } else {
             emit(Resource.Error("Проверьте подключение к интернету (код: ${response.resultCode})"))
        }
    }
}