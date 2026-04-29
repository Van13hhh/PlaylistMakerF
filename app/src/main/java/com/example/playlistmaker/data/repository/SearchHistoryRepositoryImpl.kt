package com.example.playlistmaker.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(context: Context): SearchHistoryRepository {
    private val sharedPreferences = context.getSharedPreferences(HISTORY_TRACK_KEY, Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_PREFS, null) ?: return emptyList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    @SuppressLint("UseKtx")
    override fun saveTrack(track: Track) {
        val history = getHistory().toMutableList()

        // 1. Удаляем дубликат, если он есть
        history.removeIf { it.trackId == track.trackId }

        // 2. Добавляем в начало
        history.add(0, track)

        // 3. Ограничиваем размер (например, 10)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }

        // 4. Сохраняем обратно в строку
        sharedPreferences.edit()
            .putString(HISTORY_TRACK_KEY, gson.toJson(history))
            .apply()
    }

    @SuppressLint("UseKtx")
    override fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_TRACK_KEY).apply()
    }

    companion object {
        private const val HISTORY_PREFS = "playlist_maker_history"
        private const val HISTORY_TRACK_KEY = "history_key"
        private const val MAX_HISTORY_SIZE = 10
    }
}
