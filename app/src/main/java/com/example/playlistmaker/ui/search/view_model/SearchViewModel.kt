package com.example.playlistmaker.ui.search.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.search.model.TrackSearchResult
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<TrackState>()
    fun observeState(): LiveData<TrackState> = stateLiveData

    private var searchJob: Job? = null


    private var latestSearchText: String? = null

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchTrack(changedText)
        }

    init {
        loadHistory()
    }

    fun searchDebounce(forceSearch: Boolean, changedText: String) {
        if ((latestSearchText == changedText  && !forceSearch) ) {
            return
        }
        latestSearchText = changedText
        trackSearchDebounce(changedText)
    }

    fun searchTrack(newSearchText: String) {
        Log.d("SearchFlow", "searchTrack called with: $newSearchText")

        if (newSearchText.isEmpty()) {
            loadHistory()
            return
        }
        searchJob?.cancel()
        renderState(TrackState.Loading)
        Log.d("SearchFlow", "State set to Loading")

        searchJob = viewModelScope.launch {
            Log.d("SearchFlow", "Coroutine launched")
            trackInteractor
                .searchTrack(newSearchText)
                .collect { pair ->
                    Log.d("SearchFlow", "Collect received: first=${pair.first}, second=${pair.second}")
                    processResult(pair.first, pair.second)
                }
        }
    }

    fun processResult(
        foundTrack: TrackSearchResult?,
        errorMessage: String?
    ) {
        val tracks = mutableListOf<Track>()
        if (foundTrack != null){
            tracks.addAll(foundTrack.tracks)
        }

        when{
            errorMessage != null -> {
                renderState(TrackState.Error)
            }
            tracks.isEmpty() -> {
                renderState(TrackState.Empty)
            }
            else -> {
                renderState(TrackState.Content(tracks))
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            stateLiveData.postValue(
                TrackState.History(
                    searchHistoryInteractor.getHistory()
                )
            )
        }
    }

    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch{
            searchHistoryInteractor.saveTrack(track)
            loadHistory()
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        loadHistory()
    }

    private fun renderState(state: TrackState) {
        stateLiveData.postValue(state)
    }

    sealed interface TrackState {
        object Loading : TrackState

        data class Content(
            val tracks: List<Track>
        ) : TrackState

        object Error : TrackState

        object Empty : TrackState
        data class History(
            val tracks: List<Track>
        ) : TrackState
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}
