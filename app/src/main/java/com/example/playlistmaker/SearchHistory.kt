package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections.emptyList
class SearchHistory(val sharedPreference: SharedPreferences) {
    private val listOfHistoryTracks = mutableListOf<Track>()
    fun addToList(track: Track){

        val currentList = getFromSharedPreference().toMutableList()

        currentList.removeAll { it.trackId == track.trackId }

        currentList.add(0, track)

        if (currentList.size > 10) {
            currentList.removeAt(currentList.lastIndex)
        }


        val json = Gson().toJson(currentList)
        sharedPreference.edit()
            .putString(HISTORY_TRACK_KEY, json)
            .apply()
    }

    fun getFromSharedPreference(): MutableList<Track>{
        val json = sharedPreference.getString(HISTORY_TRACK_KEY, null) ?: return emptyList()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun checkSameTrack(track: Track): Boolean{
        listOfHistoryTracks.forEach {
            if (it.trackId == track.trackId)
                return true
        }
        return false
    }

    fun clearHistory(){
        sharedPreference.edit()
            .remove(HISTORY_TRACK_KEY)
            .apply()
    }
}