package com.example.playlistmaker.ui.audioplayer.view_model

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.converters.TrackConverter
import com.example.playlistmaker.ui.audioplayer.TrackUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class AudioPlayerViewModel(
    private val trackConverter: TrackConverter,
    private val mediaPlayer: MediaPlayer,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(
        PlayerState.PlayingState(
            state = STATE_DEFAULT,
            time = formatTime(0)
        )
    )

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        updateState(newState = STATE_PREPARED, newTime = formatTime(0))
    }

    private fun updateState(
        newState: Int? = null,
        newTime: String? = null
    ) {
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
        pauseTimer()
        mediaPlayer.pause()
        updateState(newState = STATE_PAUSED, newTime = formatTime(mediaPlayer.currentPosition))
    }

    private fun updateProgress() {
        updateState(newTime = formatTime(mediaPlayer.currentPosition))
    }

    private fun startPlayer() {
        try {
            mediaPlayer.start()
            updateState(
                newState = STATE_PLAYING,
                newTime = formatTime(mediaPlayer.currentPosition)
            )
            startTimer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun stopAndResetPlayer() {
        pauseTimer()
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
        } catch (e: Exception) {
            e.printStackTrace()
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
            val time: String,
            val isFavorite: Boolean = false
        ) : PlayerState

        data class Track(
            val track: TrackUiModel,
            val isFavorite: Boolean = false
        ) : PlayerState
    }

    fun onFavoriteClicked(track: Track?) {
        if (track == null) return

        viewModelScope.launch {
            track.isFavorite = !track.isFavorite

            if (track.isFavorite) {
                playerInteractor.addToFavourite(track)
            } else {
                playerInteractor.deleteFromFavourite(track)
            }

            when (val currentState = playerStateLiveData.value) {
                is PlayerState.PlayingState -> {
                    playerStateLiveData.postValue(
                        currentState.copy(isFavorite = track.isFavorite)
                    )
                }
                is PlayerState.Track -> {
                    playerStateLiveData.postValue(
                        PlayerState.Track(
                            track = currentState.track,
                            isFavorite = track.isFavorite
                        )
                    )
                }
                else -> {}
            }
        }
    }

    fun onPlayButtonClick() {
        when (val state = playerStateLiveData.value) {
            is PlayerState.PlayingState -> {
                when (state.state) {
                    STATE_PLAYING -> pausePlayer()
                    STATE_PREPARED, STATE_PAUSED -> startPlayer()
                }
            }
            else -> startPlayer()
        }
    }

    fun loadTrack(track: Track) {
        val trackUiModel = trackConverter.convert(track)
        stopAndResetPlayer()

        playerStateLiveData.postValue(
            PlayerState.Track(
                track = trackUiModel,
                isFavorite = track.isFavorite
            )
        )

        try {
            mediaPlayer.apply {
                setDataSource(track.previewUrl)
                prepareAsync()
                setOnPreparedListener {
                    playerStateLiveData.postValue(
                        PlayerState.PlayingState(
                            STATE_PREPARED,
                            formatTime(0),
                            isFavorite = track.isFavorite
                        )
                    )
                }
                setOnCompletionListener {
                    val currentFavorite = when (val state = playerStateLiveData.value) {
                        is PlayerState.PlayingState -> state.isFavorite
                        is PlayerState.Track -> state.isFavorite
                        else -> false
                    }
                    playerStateLiveData.postValue(
                        PlayerState.PlayingState(
                            STATE_PREPARED,
                            formatTime(0),
                            isFavorite = currentFavorite
                        )
                    )
                    resetTimer()
                }
                setOnErrorListener { _, _, _ -> true }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        pauseTimer()
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startTimer() {
        pauseTimer()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(DELAY_MILLS.milliseconds)
                updateProgress()
            }
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