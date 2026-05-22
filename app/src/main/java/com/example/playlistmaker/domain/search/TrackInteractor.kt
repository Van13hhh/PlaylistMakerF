package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface TrackInteractor {
    fun searchTrack(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?, resultCode: Int)
    }
}