package com.example.playlistmaker.ui.audioplayer.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.TrackConverter
import com.example.playlistmaker.ui.audioplayer.activity.TrackUiModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val trackConverter: TrackConverter,
    private var mediaPlayer: MediaPlayer?
) : ViewModel() {
    private val playerStateLiveData = MutableLiveData<PlayerState>(
        PlayerState.PlayingState(
            state = STATE_DEFAULT,
            time = formatTime(0)
        )
    )

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val currentState = playerStateLiveData.value
            if (currentState is PlayerState.PlayingState && currentState.state == STATE_PLAYING) {
                updateProgress()  // Обновляем время
                handler.postDelayed(this, DELAY_MILLS)  // Планируем следующий запуск
            }
        }
    }

    fun resetTimer() {
        handler.removeCallbacks(updateTimeRunnable)
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
        mediaPlayer?.pause()
        updateState(newState = STATE_PAUSED, newTime = formatTime(mediaPlayer?.currentPosition))
    }

    private fun updateProgress() {
        updateState(newTime = formatTime(mediaPlayer?.currentPosition))
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        updateState(newState = STATE_PLAYING, newTime = formatTime(mediaPlayer?.currentPosition ?: 0))
        startTimer()
    }

    private fun pauseTimer() {
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun formatTime(millis: Int?): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis ?: 0)
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
        when (val state = playerStateLiveData.value) {
            is PlayerState.PlayingState -> {
                when (state.state) {
                    STATE_PLAYING -> pausePlayer()
                    STATE_PREPARED, STATE_PAUSED -> startPlayer()
                }
            }

            else -> {
                startPlayer()
            }
        }
    }

    fun loadTrack(track: Track) {
        val trackUiModel = trackConverter.convert(track)
        releasePlayer()
        playerStateLiveData.postValue(PlayerState.Track(trackUiModel))
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            prepareAsync()
            setOnPreparedListener {
                playerStateLiveData.postValue(
                    PlayerState.PlayingState(
                        STATE_PREPARED, formatTime(0)
                    )
                )
            }
            setOnCompletionListener {
                playerStateLiveData.postValue(
                    PlayerState.PlayingState(
                        STATE_PREPARED, formatTime(0)
                    )
                )
                resetTimer()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pauseTimer()
        releasePlayer()
    }

    fun startTimer() {
        pauseTimer()
        handler.postDelayed(updateTimeRunnable, DELAY_MILLS)
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val DELAY_MILLS = 500L
    }

}