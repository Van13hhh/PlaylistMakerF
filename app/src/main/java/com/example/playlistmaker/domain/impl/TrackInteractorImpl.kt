package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.interactors.TrackInteractor
import com.example.playlistmaker.domain.repository.TrackRepository

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun searchTrack(
        expression: String,
        consumer: TrackInteractor.TrackConsumer
    ) {
        val t = Thread {
            val searchResult = repository.searchTrack(expression)
            consumer.consume(searchResult.tracks, searchResult.resultCode)
        }
        t.start()
    }
}