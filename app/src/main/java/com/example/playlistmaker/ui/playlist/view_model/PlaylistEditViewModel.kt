package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.creating_playlist.view_model.PlaylistCreateViewModel
import kotlinx.coroutines.launch

class PlaylistEditViewModel(
    private val playlistInteractor: PlaylistInteractor
) : PlaylistCreateViewModel(playlistInteractor) {

    private val _state = MutableLiveData<PlaylistEditState>()
    val state: LiveData<PlaylistEditState> = _state

    fun initPlaylist(playlist: Playlist) {
        _state.value = PlaylistEditState.Content(playlist)
    }

    override fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }
}

sealed class PlaylistEditState {
    data class Content(val playlist: Playlist) : PlaylistEditState()
}