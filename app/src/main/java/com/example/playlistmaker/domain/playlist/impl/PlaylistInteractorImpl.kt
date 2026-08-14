package com.example.playlistmaker.domain.playlist.impl

import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.PlaylistRepository
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.TrackUiModel
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistTrackIds(playListId: Long): List<Long> {
        return repository.getPlaylistTrackIds(playListId)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        return repository.getPlaylist(id)
    }

    override fun getPlaylistTrack(listOfTracksId: List<Long>): Flow<List<TrackUiModel>> {
        return repository.getPlaylistTrack(listOfTracksId)
    }

    override suspend fun deleteTrack(
        trackId: Long,
        playlistId: Long
    ) {
        repository.deleteTrack(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }
}