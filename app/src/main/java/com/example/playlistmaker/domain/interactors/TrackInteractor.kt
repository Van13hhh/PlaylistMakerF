package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.models.Track

interface TrackInteractor {
    fun searchTrack(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>, resultCode: Int)
    }
}