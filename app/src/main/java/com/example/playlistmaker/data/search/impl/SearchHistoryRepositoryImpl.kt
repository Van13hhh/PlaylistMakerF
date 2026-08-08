package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.search.storage.StorageClient
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.search.SearchHistoryRepository
class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<List<Track>>,
    private val appDatabase: AppDatabase
    ) :
    SearchHistoryRepository {
    override suspend fun getHistory(): List<Track> {
        return storage.getData()?.map { track ->
            Track(
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
                appDatabase.trackDao().getFavouriteTracksId()?.contains(track.trackId) ?: false
            )

        } ?: emptyList()
    }

    override suspend fun saveTrack(track: Track) {
        val history = getHistory().toMutableList()

        history.removeIf { it.trackId == track.trackId }

        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }

        storage.storeData(history.toList())
    }

    override fun clearHistory() {
        storage.clearData()
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}