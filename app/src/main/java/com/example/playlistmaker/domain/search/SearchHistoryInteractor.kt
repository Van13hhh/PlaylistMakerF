package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>
    fun saveTrack(track: Track)
    fun clearHistory()
}