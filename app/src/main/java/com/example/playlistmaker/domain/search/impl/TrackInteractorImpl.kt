package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.TrackRepository
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