package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.TrackSearchResult

interface TrackRepository {
    fun searchTrack(expression: String): TrackSearchResult
}