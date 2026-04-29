package com.example.playlistmaker.domain.impl


import com.example.playlistmaker.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.domain.repository.AudioPlayerRepository

class AudioPlayerInteractorImpl(
    private val audioPlayerRepository: AudioPlayerRepository
): AudioPlayerInteractor {
    override fun prepare(
        url: String,
        onStateChanged: (AudioPlayerInteractor.PlayerState) -> Unit
    ) {
        audioPlayerRepository.prepare(url, onStateChanged)
    }

    override fun start() {
        audioPlayerRepository.start()
    }

    override fun pause() {
        audioPlayerRepository.pause()
    }

    override fun release() {
        audioPlayerRepository.release()
    }

    override fun isPlaying(): Boolean {
        return audioPlayerRepository.isPlaying()
    }

    override fun getCurrentPosition(): Int {
        return audioPlayerRepository.getCurrentPosition()
    }
}