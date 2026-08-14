package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.audioplayer.TrackUiModel
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> = stateLiveData

    private val eventsLiveData = MutableLiveData<PlaylistEvent>()
    fun observeEvents(): LiveData<PlaylistEvent> = eventsLiveData

    fun loadPlaylist(id: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylist(id)
            if (playlist.listOfTrackIds.isEmpty()) {
                stateLiveData.postValue(PlaylistState.Content(playlist, emptyList()))
            } else {
                playlistInteractor.getPlaylistTrack(playlist.listOfTrackIds)
                    .collect { trackList ->
                        stateLiveData.postValue(PlaylistState.Content(playlist, trackList))
                    }
            }
        }
    }

    fun deleteTrack(trackId: Long, playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.deleteTrack(trackId, playlistId)
            loadPlaylist(playlistId)
        }
    }

    fun deletePlaylist() {
        val currentState = stateLiveData.value
        if (currentState is PlaylistState.Content) {
            viewModelScope.launch {
                playlistInteractor.deletePlaylist(currentState.playlist)
            }
        }
    }

    fun sharePlaylist() {
        val currentState = stateLiveData.value
        if (currentState is PlaylistState.Content) {
            if (currentState.tracks.isEmpty()) {
                eventsLiveData.postValue(PlaylistEvent.ShowToast("В этом плейлисте нет списка треков, которым можно поделиться"))
            } else {
                eventsLiveData.postValue(
                    PlaylistEvent.SharePlaylist(
                        currentState.playlist,
                        currentState.tracks
                    )
                )
            }
        }
    }

    fun onMoreClick() {
        val currentState = stateLiveData.value
        if (currentState is PlaylistState.Content) {
            eventsLiveData.postValue(PlaylistEvent.MorePlaylist(currentState.playlist))
        }
    }

    sealed interface PlaylistState {
        data class Content(val playlist: Playlist, val tracks: List<TrackUiModel>) : PlaylistState
    }

    sealed interface PlaylistEvent {
        data class ShowToast(val message: String) : PlaylistEvent
        data class SharePlaylist(val playlist: Playlist, val tracks: List<TrackUiModel>) :
            PlaylistEvent

        data class MorePlaylist(val playlist: Playlist) : PlaylistEvent
    }
}