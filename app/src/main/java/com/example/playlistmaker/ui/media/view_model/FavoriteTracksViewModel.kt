package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoriteTrackState>()
    fun observeState(): LiveData<FavoriteTrackState> = stateLiveData

    fun loadFavouriteTrack(){
        viewModelScope.launch{playerInteractor.getFavoriteTracks().collect {tracks ->
            if (tracks.isNotEmpty()){
                stateLiveData.postValue(FavoriteTrackState.Content(tracks))
            }else{
                stateLiveData.postValue(FavoriteTrackState.Empty)
            }
        }
        }
    }


    sealed interface FavoriteTrackState {
        data class Content(
            val tracks: List<Track>
        ) : FavoriteTrackState

        object Empty : FavoriteTrackState
    }
}