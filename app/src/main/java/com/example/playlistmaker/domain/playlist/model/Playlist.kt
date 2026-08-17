package com.example.playlistmaker.domain.playlist.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val photoUri: Uri? = null,
    val listOfTrackIds: MutableList<Long> = mutableListOf(),
    var countTracks: Int = 0
) : Parcelable