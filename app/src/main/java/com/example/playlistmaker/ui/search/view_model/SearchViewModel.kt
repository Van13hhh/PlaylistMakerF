package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.model.Track

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    // ==================== COMPANION ====================

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SearchViewModel(app)
            }
        }
    }

    // ==================== LIVE DATA ====================

    private val stateLiveData = MutableLiveData<TrackState>()
    fun observeState(): LiveData<TrackState> = stateLiveData

    private val historyLiveData = MutableLiveData<List<Track>>()
    fun observeHistory(): LiveData<List<Track>> = historyLiveData

    // ==================== DEPENDENCIES ====================

    private val handler = Handler(Looper.getMainLooper())
    private val trackInteractor: TrackInteractor = Creator.provideTrackInteractor(application)
    private val searchHistoryInteractor = Creator.getSearchHistoryInteractor(application)

    // ==================== STATE ====================

    private var latestSearchText: String? = null

    // ==================== INIT ====================

    init {
        loadHistory()
    }

    // ==================== HISTORY ====================

    fun searchDebounce(forceSearch: Boolean, changedText: String) {
        if (latestSearchText == changedText && !forceSearch) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTrack(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun searchTrack(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TrackState.Loading)
        }

        trackInteractor.searchTrack(newSearchText, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>?, resultCode: Int) {
                handler.post {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when (resultCode) {
                        -1 -> renderState(
                                TrackState.Error
                        )

                        else -> if (tracks.isEmpty()) {
                            renderState(
                                TrackState.Empty
                            )
                        } else {
                            renderState(TrackState.Content(tracks))
                        }
                    }
                }
            }
        })
    }

    // ==================== HISTORY ====================

    fun loadHistory() {
        historyLiveData.postValue(searchHistoryInteractor.getHistory())
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.saveTrack(track)
        loadHistory()
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        loadHistory()
    }

    // ==================== RENDER ====================

    private fun renderState(state: TrackState) {
        stateLiveData.postValue(state)
    }

    // ==================== CLEANUP ====================

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    sealed interface TrackState {
        object Loading: TrackState

        data class Content(
            val tracks: List<Track>
        ): TrackState

        object Error: TrackState

        object Empty: TrackState
    }

}
