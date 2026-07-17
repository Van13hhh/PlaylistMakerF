package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.converters.TrackDbConvertor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : PlayerRepository {

    override suspend fun addToFavourite(track: Track) {
        val track = trackDbConvertor.map(track)
        appDatabase.trackDao().insertTrack(track)
    }

    override suspend fun deleteFromFavourite(track: Track) {
        val track = trackDbConvertor.map(track)
        appDatabase.trackDao().removeTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks()
            .map { entities ->
                entities.reversed()
            }
            .map { entities ->
                convertFromTrackEntity(entities)
            }
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track>{
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}