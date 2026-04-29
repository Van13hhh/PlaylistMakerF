package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.interactors.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
): SearchHistoryInteractor {
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