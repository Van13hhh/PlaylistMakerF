package com.example.playlistmaker.ui.audioplayer.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val url: String) : ViewModel() {

    private val mediaPlayer = MediaPlayer()
    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerStateLiveData.value == STATE_PLAYING) {
                updateProgress()  // Обновляем время
                handler.postDelayed(this, DELAY_MILLS)  // Планируем следующий запуск
            }
        }
    }

    init {
        preparedPlayer()
    }

    fun onPlayButtonClick() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparedPlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(STATE_PREPARED)
            resetTimer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        pauseTimer()
        mediaPlayer.release()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimer()
    }

    private fun pauseTimer() {
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun updateProgress() {
        progressTimeLiveData.postValue(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                mediaPlayer.currentPosition
            )
        )
    }

    fun startTimer() {
        pauseTimer()
        handler.postDelayed(updateTimeRunnable, DELAY_MILLS)
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
        fun getFactory(url: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(url)
            }
        }
    }

}