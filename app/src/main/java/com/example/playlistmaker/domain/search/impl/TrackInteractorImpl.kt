package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.TrackRepository
import com.example.playlistmaker.domain.search.model.TrackSearchResult
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TrackRepository) :
    TrackInteractor {

    override fun searchTrack(expression: String): Flow<Pair<TrackSearchResult?, String?>> {
        return repository.searchTrack(expression).map { result ->
            when(result) {
                is Resource.Success -> Pair(result.data, null)
                is Resource.Error -> Pair(null, result.message)
            }
        }
    }
}