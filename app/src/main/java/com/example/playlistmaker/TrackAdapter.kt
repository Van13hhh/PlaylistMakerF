package com.example.playlistmaker

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private var listOfTracks: List<Track>,
    private val sharedPreferences: SharedPreferences,
    private val onClick: (Track) -> Unit,
) : RecyclerView.Adapter<TrackViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.track_view,
            parent,
            false
        )
        return TrackViewHolder(view, sharedPreferences, onClick, { clickDebounce() })
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(listOfTracks[position])
    }

    override fun getItemCount() = listOfTracks.size

    fun updateTracks(newTracks: List<Track>) {
        listOfTracks = newTracks
        notifyDataSetChanged()
    }

    fun clickDebounce(): Boolean {
        val curent = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return curent
    }
}