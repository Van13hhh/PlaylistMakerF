package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.interactors.AudioPlayerInteractor


interface AudioPlayerRepository {
    fun prepare(url: String,
                onStateChanged: (AudioPlayerInteractor.PlayerState) -> Unit)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}