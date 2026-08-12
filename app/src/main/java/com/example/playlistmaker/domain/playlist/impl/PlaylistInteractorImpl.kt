package com.example.playlistmaker.domain.playlist.impl

import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.PlaylistRepository
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistTrackIds(playListId: Long): List<Long> {
        return repository.getPlaylistTrackIds(playListId)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }
}