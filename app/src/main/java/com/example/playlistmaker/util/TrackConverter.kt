package com.example.playlistmaker.util

import com.example.playlistmaker.domain.player.model.TrackUiModel
import com.example.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

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
    private fun formatTime(timeMillis: Long): String {
        return try {
            SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(timeMillis)
        } catch (_: Exception) {
            "00:00"
        }
    }

    private fun formatDate(releaseDate: String?): String{
        return try {
            releaseDate?.take(4) ?: ""
        } catch (_: Exception){
            ""
        }
    }
}