package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    suspend fun addToFavourite(track: Track)
    suspend fun deleteFromFavourite(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
}