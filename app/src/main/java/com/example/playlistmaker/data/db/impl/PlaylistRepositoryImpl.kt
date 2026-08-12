package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.playlist.PlaylistRepository
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.converters.PlaylistDbConvertor
import com.example.playlistmaker.util.converters.PlaylistTrackConvertor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistTrackConvertor: PlaylistTrackConvertor
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        val playlist = playlistDbConvertor.convert(playlist)
        appDatabase.playlistDao().insertPlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists()
            .map { playlistEntities ->
                playlistEntities.map { entity ->
                    playlistDbConvertor.convert(entity)
                }
            }
    }

    override suspend fun getPlaylistTrackIds(playListId: Long): List<Long> {
        val jsonString = appDatabase.playlistDao().getListWithTrackIds(playListId)
        return playlistDbConvertor.convert(jsonString)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val updatePlaylist = playlistDbConvertor.convert(playlist.apply {
            countTracks++
            listOfTrackIds.add(track.trackId.toLong())
        })
        appDatabase.playlistDao().insertPlaylist(updatePlaylist)
        appDatabase.PlaylistTracksDao().insertTrack(playlistTrackConvertor.map(track))
    }
}