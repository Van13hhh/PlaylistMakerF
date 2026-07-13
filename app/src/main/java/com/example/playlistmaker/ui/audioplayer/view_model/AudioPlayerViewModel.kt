package com.example.playlistmaker.ui.audioplayer.view_model

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.TrackConverter
import com.example.playlistmaker.ui.audioplayer.TrackUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class AudioPlayerViewModel(
    private val trackConverter: TrackConverter,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(
        PlayerState.PlayingState(
            state = STATE_DEFAULT,
            time = formatTime(0)
        )
    )

    init {
        Log.d("PlayerLog", "ViewModel created, mediaPlayer: ${mediaPlayer.hashCode()}")
    }

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    fun resetTimer() {
        Log.d("PlayerLog", "resetTimer called")
        timerJob?.cancel()
        timerJob = null
        updateState(newState = STATE_PREPARED, newTime = formatTime(0))
    }

    private fun updateState(
        newState: Int? = null,
        newTime: String? = null
    ) {
        Log.d("PlayerLog", "updateState: newState=$newState, newTime=$newTime")
        val updatedState = when (val currentState = playerStateLiveData.value) {
            is PlayerState.PlayingState -> {
                currentState.copy(
                    state = newState ?: currentState.state,
                    time = newTime ?: currentState.time
                )
            }

            else -> {
                PlayerState.PlayingState(
                    state = newState ?: STATE_DEFAULT,
                    time = newTime ?: formatTime(0)
                )
            }
        }

        playerStateLiveData.postValue(updatedState)
    }

    private fun pausePlayer() {
        Log.d("PlayerLog", "pausePlayer called, isPlaying: ${mediaPlayer.isPlaying}")
        pauseTimer()
        mediaPlayer.pause()
        updateState(newState = STATE_PAUSED, newTime = formatTime(mediaPlayer.currentPosition))
    }

    private fun updateProgress() {
        Log.d("PlayerLog", "updateProgress: position=${mediaPlayer.currentPosition}")
        updateState(newTime = formatTime(mediaPlayer.currentPosition))
    }

    private fun startPlayer() {
        Log.d("PlayerLog", "startPlayer called, isPlaying: ${mediaPlayer.isPlaying}")
        try {
            mediaPlayer.start()
            updateState(
                newState = STATE_PLAYING,
                newTime = formatTime(mediaPlayer.currentPosition)
            )
            startTimer()
        } catch (e: Exception) {
            Log.e("PlayerLog", "startPlayer error: ${e.message}", e)
        }
    }

    private fun pauseTimer() {
        Log.d("PlayerLog", "pauseTimer called, timerJob=$timerJob")
        timerJob?.cancel()
        timerJob = null
    }

    private fun stopAndResetPlayer() {
        Log.d("PlayerLog", "stopAndResetPlayer called, isPlaying: ${mediaPlayer.isPlaying}")
        pauseTimer()
        try {
            if (mediaPlayer.isPlaying) {
                Log.d("PlayerLog", "Stopping player")
                mediaPlayer.stop()
            }
            Log.d("PlayerLog", "Resetting player")
            mediaPlayer.reset()
            Log.d("PlayerLog", "Player reset complete")
        } catch (e: Exception) {
            Log.e("PlayerLog", "stopAndResetPlayer error: ${e.message}", e)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(millis: Int?): String {
        val totalSeconds = (millis ?: 0) / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    sealed interface PlayerState {
        data class PlayingState(
            val state: Int,
            val time: String
        ) : PlayerState

        data class Track(
            val track: TrackUiModel
        ) : PlayerState
    }

    fun onPlayButtonClick() {
        Log.d("PlayerLog", "onPlayButtonClick called, currentState=${playerStateLiveData.value}")
        when (val state = playerStateLiveData.value) {
            is PlayerState.PlayingState -> {
                when (state.state) {
                    STATE_PLAYING -> {
                        Log.d("PlayerLog", "STATE_PLAYING -> pause")
                        pausePlayer()
                    }
                    STATE_PREPARED, STATE_PAUSED -> {
                        Log.d("PlayerLog", "STATE_PREPARED/PAUSED -> start")
                        startPlayer()
                    }
                }
            }

            else -> {
                Log.d("PlayerLog", "Other state -> start")
                startPlayer()
            }
        }
    }

    fun loadTrack(track: Track) {
        Log.d("PlayerLog", "loadTrack called: trackId=${track.trackId}, trackName=${track.trackName}")
        val trackUiModel = trackConverter.convert(track)
        Log.d("PlayerLog", "Calling stopAndResetPlayer")
        stopAndResetPlayer()
        playerStateLiveData.postValue(PlayerState.Track(trackUiModel))
        Log.d("PlayerLog", "Track posted, setting DataSource: ${track.previewUrl}")
        try {
            mediaPlayer.apply {
                setDataSource(track.previewUrl)
                Log.d("PlayerLog", "setDataSource done, calling prepareAsync")
                prepareAsync()
                setOnPreparedListener {
                    Log.d("PlayerLog", "onPrepared called")
                    playerStateLiveData.postValue(
                        PlayerState.PlayingState(
                            STATE_PREPARED, formatTime(0)
                        )
                    )
                }
                setOnCompletionListener {
                    Log.d("PlayerLog", "onCompletion called")
                    playerStateLiveData.postValue(
                        PlayerState.PlayingState(
                            STATE_PREPARED, formatTime(0)
                        )
                    )
                    resetTimer()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("PlayerLog", "onError: what=$what, extra=$extra")
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("PlayerLog", "loadTrack error: ${e.message}", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("PlayerLog", "onCleared called")
        pauseTimer()
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            Log.d("PlayerLog", "Player reset in onCleared")
        } catch (e: Exception) {
            Log.e("PlayerLog", "onCleared error: ${e.message}", e)
        }
    }

    fun startTimer() {
        Log.d("PlayerLog", "startTimer called")
        pauseTimer()
        timerJob = viewModelScope.launch {
            Log.d("PlayerLog", "Timer coroutine started, isPlaying: ${mediaPlayer.isPlaying}")
            while (mediaPlayer.isPlaying) {
                delay(DELAY_MILLS.milliseconds)
                updateProgress()
            }
            Log.d("PlayerLog", "Timer coroutine ended (isPlaying=false)")
        }
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val DELAY_MILLS = 300L
    }
}