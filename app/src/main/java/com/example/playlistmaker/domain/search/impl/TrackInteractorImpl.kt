package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.TrackRepository
import java.util.concurrent.Executor

class TrackInteractorImpl(private val repository: TrackRepository, private val executor: Executor) : TrackInteractor {
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