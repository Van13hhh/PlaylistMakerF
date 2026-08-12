package com.example.playlistmaker.util.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor(private val gson: Gson) {
    fun convert(playlist: PlaylistEntity): Playlist {
        val trackIds = try {
            val type = object : TypeToken<List<Long>>() {}.type
            gson.fromJson<List<Long>>(playlist.listOfTrackIds, type)
        } catch (_: Exception) {
            emptyList()
        }

        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            photoPath = playlist.photoPath,
            listOfTrackIds = trackIds.toMutableList(),
            countTracks = playlist.countTracks
        )
    }

    fun convert(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            photoPath = playlist.photoPath,
            listOfTrackIds = gson.toJson(playlist.listOfTrackIds),
            countTracks = playlist.countTracks
        )
    }

    fun convert(listTrackIds: String?): List<Long> {
        return gson.fromJson(listTrackIds, Array<Long>::class.java).toList()
    }
}