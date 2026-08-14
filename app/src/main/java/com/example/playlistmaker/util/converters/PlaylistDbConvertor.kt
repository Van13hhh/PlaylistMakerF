package com.example.playlistmaker.util.converters

import android.net.Uri
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.net.toUri

class PlaylistDbConvertor(private val gson: Gson) {

    fun convert(playlist: PlaylistEntity): Playlist {
        val trackIds = try {
            val type = object : TypeToken<List<Long>>() {}.type
            gson.fromJson<List<Long>>(playlist.listOfTrackIds, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        val photoUri = playlist.photoPath?.let { path ->
            try {
                path.toUri()
            } catch (_: Exception) {
                null
            }
        }

        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            photoUri = photoUri,
            listOfTrackIds = trackIds.toMutableList(),
            countTracks = playlist.countTracks
        )
    }

    fun convert(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            photoPath = playlist.photoUri?.toString(),
            listOfTrackIds = gson.toJson(playlist.listOfTrackIds),
            countTracks = playlist.countTracks
        )
    }

    fun convert(listTrackIds: String?): List<Long> {
        return try {
            gson.fromJson(listTrackIds, Array<Long>::class.java)?.toList() ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}