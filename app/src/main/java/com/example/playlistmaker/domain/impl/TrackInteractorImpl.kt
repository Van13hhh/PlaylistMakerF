package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.interactors.TrackInteractor
import com.example.playlistmaker.domain.repository.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    private val executor = Executors.newFixedThreadPool(2)
    override fun searchTrack(
        expression: String,
        consumer: TrackInteractor.TrackConsumer
    ) {

        executor.execute {
            val searchResult = repository.searchTrack(expression)
            consumer.consume(searchResult.tracks, searchResult.resultCode)
        }
    }
}