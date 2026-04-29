package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.domain.repository.AudioPlayerRepository

class AudioPlayerRepositoryImpl: AudioPlayerRepository {
    private val mediaPlayer = MediaPlayer()
    private var currentState = AudioPlayerInteractor.PlayerState.STATE_DEFAULT
    private var onStateChanged: ((AudioPlayerInteractor.PlayerState) -> Unit)? = null

     override fun prepare(
        url: String,
        onStateChanged: (AudioPlayerInteractor.PlayerState) -> Unit
    ) {
        this.onStateChanged = onStateChanged

        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            updateState(AudioPlayerInteractor.PlayerState.STATE_PREPARED)
        }

        mediaPlayer.setOnCompletionListener {
            updateState(AudioPlayerInteractor.PlayerState.STATE_PREPARED)
        }
    }

    override fun start() {
        if (currentState != AudioPlayerInteractor.PlayerState.STATE_DEFAULT) {
            mediaPlayer.start()
            updateState(AudioPlayerInteractor.PlayerState.STATE_PLAYING)
        }
    }

    override fun pause() {
        if (currentState == AudioPlayerInteractor.PlayerState.STATE_PLAYING) {
            mediaPlayer.pause()
            updateState(AudioPlayerInteractor.PlayerState.STATE_PAUSED)
        }
    }

    override fun release() {
        mediaPlayer.release()
        onStateChanged = null

        mediaPlayer.setOnPreparedListener(null)
        mediaPlayer.setOnCompletionListener(null)

        currentState = AudioPlayerInteractor.PlayerState.STATE_DEFAULT
    }

    override fun getCurrentPosition(): Int {
        return try {
            if (currentState == AudioPlayerInteractor.PlayerState.STATE_PREPARED ||
                currentState == AudioPlayerInteractor.PlayerState.STATE_DEFAULT
            ) {
                0
            } else {
                mediaPlayer.currentPosition
            }
        } catch (_: Exception) {
            0
        }
    }

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    private fun updateState(state: AudioPlayerInteractor.PlayerState) {
        currentState = state
        onStateChanged?.invoke(currentState)
    }
}