package com.example.playlistmaker.domain.playlist

import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistTrackIds(playListId: Long): List<Long>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

}