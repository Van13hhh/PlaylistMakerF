package com.example.playlistmaker.util

import android.annotation.SuppressLint
import com.example.playlistmaker.ui.audioplayer.TrackUiModel
import com.example.playlistmaker.domain.search.model.Track

class TrackConverter {
    fun convert(track: Track): TrackUiModel {
        return TrackUiModel(
            track.trackName,
            track.artistName,
            formatTime(track.trackTimeMillis),
            track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
            track.trackId,
            track.collectionName,
            formatDate(track.releaseDate),
            track.primaryGenreName,
            track.country,
            track.previewUrl
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