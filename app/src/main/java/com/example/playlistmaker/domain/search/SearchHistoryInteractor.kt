package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryInteractor {
    suspend fun getHistory(): List<Track>
    suspend fun saveTrack(track: Track)
    fun clearHistory()
}