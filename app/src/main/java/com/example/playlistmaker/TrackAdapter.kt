package com.example.playlistmaker

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private var listOfTracks: List<Track>,
    private val sharedPreferences: SharedPreferences,
    private val onClick: (Track) -> Unit
): RecyclerView.Adapter<TrackViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.track_view,
            parent,
            false
        )
        return TrackViewHolder(view, sharedPreferences, onClick)
    }
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(listOfTracks[position])
    }

    override fun getItemCount() = listOfTracks.size

    fun updateTracks(newTracks: List<Track>){
        listOfTracks = newTracks
        notifyDataSetChanged()
    }
}