package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlayerInteractorImpl(
    private val repository: PlayerRepository
): PlayerInteractor {
    override suspend fun addToFavourite(track: Track) {
        repository.addToFavourite(track)
    }

    override suspend fun deleteFromFavourite(track: Track) {
        repository.deleteFromFavourite(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }
}