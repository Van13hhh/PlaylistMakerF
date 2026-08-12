package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): MutableLiveData<PlaylistState> = stateLiveData

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    stateLiveData.value = PlaylistState.Empty
                } else {
                    stateLiveData.value = PlaylistState.Content(playlists)
                }
            }
        }
    }

    fun updateUi() {
        loadPlaylists()
    }

    sealed interface PlaylistState {
        data class Content(val playlists: List<Playlist>) : PlaylistState
        object Empty : PlaylistState
    }
}