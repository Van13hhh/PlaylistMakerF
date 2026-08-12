package com.example.playlistmaker.util.converters

import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.search.model.Track

class PlaylistTrackConvertor {
    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
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
}