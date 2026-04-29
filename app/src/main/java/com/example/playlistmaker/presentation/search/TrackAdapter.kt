package com.example.playlistmaker.presentation.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(
    private var listOfTracks: List<Track>,
    private val onClick: (Track) -> Unit,
) : RecyclerView.Adapter<TrackViewHolder>() {

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
        return TrackViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(listOfTracks[position])
    }

    override fun getItemCount() = listOfTracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTracks(newTracks: List<Track>) {
        listOfTracks = newTracks
        notifyDataSetChanged()
    }
}