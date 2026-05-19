package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.storage.StorageClient
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.search.SearchHistoryRepository

class SearchHistoryRepositoryImpl(private val storage : StorageClient<List<Track>>): SearchHistoryRepository {

    override fun getHistory(): List<Track> {
        return storage.getData() ?: emptyList()
    }

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
        storage.storeData(history.toList())
    }

    override fun clearHistory() {
        storage.clearData()
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}