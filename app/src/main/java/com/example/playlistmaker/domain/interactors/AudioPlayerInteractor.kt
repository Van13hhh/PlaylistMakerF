package com.example.playlistmaker.domain.interactors

interface AudioPlayerInteractor {
    fun prepare(url: String, onStateChanged: (PlayerState) -> Unit)
    fun start()
    fun pause()
    fun release()
    fun  isPlaying(): Boolean
    fun getCurrentPosition(): Int
    enum class PlayerState {
        STATE_DEFAULT,
        STATE_PREPARED,
        STATE_PLAYING,
        STATE_PAUSED
    }
}