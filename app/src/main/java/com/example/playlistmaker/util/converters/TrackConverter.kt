package com.example.playlistmaker.util.converters

import android.annotation.SuppressLint
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.TrackUiModel

class TrackConverter {
    fun convert(track: Track): TrackUiModel {
        return TrackUiModel(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = formatTime(track.trackTimeMillis),
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite
        )
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(timeMillis: Long): String {
        val totalSeconds = timeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun formatDate(releaseDate: String?): String {
        return try {
            releaseDate?.take(4) ?: ""
        } catch (_: Exception) {
            ""
        }
    }
}