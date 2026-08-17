package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.domain.playlist.PlaylistRepository
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.TrackUiModel
import com.example.playlistmaker.util.converters.PlaylistDbConvertor
import com.example.playlistmaker.util.converters.PlaylistTrackConvertor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTracksDao: PlaylistTracksDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistTrackConvertor: PlaylistTrackConvertor,
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        val playlist = playlistDbConvertor.convert(playlist)
        playlistDao.insertPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists()
            .map { playlistEntities ->
                playlistEntities.map { entity ->
                    playlistDbConvertor.convert(entity)
                }
            }
    }

    override suspend fun getPlaylistTrackIds(playListId: Long): List<Long> {
        val jsonString = playlistDao.getListWithTrackIds(playListId)
        return playlistDbConvertor.convert(jsonString)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val updatePlaylist = playlistDbConvertor.convert(playlist.apply {
            countTracks++
            listOfTrackIds.add(track.trackId.toLong())
        })
        playlistDao.insertPlaylist(updatePlaylist)
        playlistTracksDao.insertTrack(playlistTrackConvertor.map(track))
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        return playlistDbConvertor.convert(playlistDao.getPlaylist(id))
    }

    override fun getPlaylistTrack(listOfTracksId: List<Long>): Flow<List<TrackUiModel>> {
        return playlistTracksDao.getTracks()
            .map { listOfTracks ->
                listOfTracks
                    .filter { listOfTracksId.contains(it.trackId.toLong()) }
                    .map { track ->
                        playlistTrackConvertor.map(track)
                    }
            }
            .distinctUntilChanged()
    }

    override suspend fun deleteTrack(trackId: Long, playlistId: Long) {
        val playlist = playlistDbConvertor.convert(playlistDao.getPlaylist(playlistId))

        playlistDao.insertPlaylist(
            playlistDbConvertor.convert(
                playlist.apply {
                    listOfTrackIds.remove(trackId)
                    countTracks = listOfTrackIds.size
                }
            )
        )
        checkUnnecessaryTrack(trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val tracksId = playlist.listOfTrackIds
        playlistDao.deleteTrack(playlistDbConvertor.convert(playlist))
        tracksId.map { tracksId ->
            checkUnnecessaryTrack(tracksId)
        }
    }

    private suspend fun checkUnnecessaryTrack(trackId: Long) {
        val allTrackIds = playlistDao.getAllListOfTracks()
            .flatMap { jsonString ->
                playlistDbConvertor.convert(jsonString)
            }

        if (trackId !in allTrackIds) {
            playlistTracksDao.deletePlaylistById(trackId)
        }
    }
}