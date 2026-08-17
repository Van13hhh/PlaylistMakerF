package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.search.model.Track

class TrackAdapter(
    private val clickListener: TrackClickListener,
    private val longClickListener: LongClickListener? = null

) : RecyclerView.Adapter<TrackViewHolder>() {

    var listOfTracks: MutableList<Track> = mutableListOf()

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder.from(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(listOfTracks[position])
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(listOfTracks[position])
        }

        holder.itemView.setOnLongClickListener {
            longClickListener?.onTrackLongClick(listOfTracks[position]) ?: false
        }
    }

    override fun getItemCount() = listOfTracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTracks(newTracks: List<Track>) {
        listOfTracks.clear()
        listOfTracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    fun interface TrackClickListener {
        fun onTrackClick(movie: Track)
    }

    interface LongClickListener {
        fun onTrackLongClick(track: Track): Boolean
    }
}