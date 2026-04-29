package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun getHistory(): List<Track>
    fun saveToHistory(track: Track)
    fun clearHistory()
}