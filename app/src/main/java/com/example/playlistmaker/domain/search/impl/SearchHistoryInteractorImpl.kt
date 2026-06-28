package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.search.SearchHistoryRepository

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {
    override fun getHistory(): List<Track> {
        return searchHistoryRepository.getHistory()
    }

    override fun saveTrack(track: Track) {
        searchHistoryRepository.saveTrack(track)
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }
}