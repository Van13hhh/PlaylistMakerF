package com.example.playlistmaker.util.converters

import android.util.Log
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.search.model.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        Log.d("PlayerLog", "💾 Сохранение в БД: ${track.trackName}, isFavorite = ${track.isFavorite}")
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite
        )
    }

    fun map(track: TrackEntity): Track {
        Log.d("PlayerLog", "📖 Чтение из БД: ${track.trackName}, isFavorite = ${track.isFavorite}")
        return Track(
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite
        )
    }
}