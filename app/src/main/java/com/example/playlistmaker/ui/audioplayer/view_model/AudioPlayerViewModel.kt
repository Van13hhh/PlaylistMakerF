package com.example.playlistmaker.ui.audioplayer.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.TrackConverter
import com.example.playlistmaker.ui.search.TrackUiModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val trackConverter: TrackConverter) : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val trackUiModelLiveData = MutableLiveData<TrackUiModel>()
    fun observeTrackUiModel(): LiveData<TrackUiModel> = trackUiModelLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerStateLiveData.value == STATE_PLAYING) {
                updateProgress()  // Обновляем время
                handler.postDelayed(this, DELAY_MILLS)  // Планируем следующий запуск
            }
        }
    }

    fun onPlayButtonClick() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    fun loadTrack(track: Track) {
        val trackUiModel = trackConverter.convert(track)
        trackUiModelLiveData.postValue(trackUiModel)

        releasePlayer()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            prepareAsync()
            setOnPreparedListener {
                playerStateLiveData.postValue(STATE_PREPARED)
            }
            setOnCompletionListener {
                playerStateLiveData.postValue(STATE_PREPARED)
                resetTimer()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pauseTimer()
        releasePlayer()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer?.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimer()
    }

    private fun pauseTimer() {
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun updateProgress() {
        progressTimeLiveData.postValue(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                mediaPlayer?.currentPosition
            )
        )
    }

    fun startTimer() {
        pauseTimer()
        handler.postDelayed(updateTimeRunnable, DELAY_MILLS)
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun resetTimer() {
        handler.removeCallbacks(updateTimeRunnable)
        progressTimeLiveData.postValue(SimpleDateFormat("mm:ss", Locale.getDefault()).format(0))
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val DELAY_MILLS = 500L
    }

}