package com.example.playlistmaker.domain.playlist

import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.TrackUiModel
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistTrackIds(playListId: Long): List<Long>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    suspend fun getPlaylist(id: Long): Playlist

    fun getPlaylistTrack(listOfTracksId: List<Long>): Flow<List<TrackUiModel>>

    suspend fun deleteTrack(trackId: Long, playlistId: Long)

    suspend fun deletePlaylist(playlist: Playlist)
}