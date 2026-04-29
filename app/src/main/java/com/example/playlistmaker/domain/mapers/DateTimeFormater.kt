package com.example.playlistmaker.domain.mapers

import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeFormater {
    private val format = SimpleDateFormat("mm:ss", Locale.getDefault())

    fun format(millis: Int): String {
        return format.format(millis)
    }
}