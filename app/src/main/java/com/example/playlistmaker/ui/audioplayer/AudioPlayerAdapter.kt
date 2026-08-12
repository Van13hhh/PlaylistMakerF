package com.example.playlistmaker.ui.audioplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.playlist.model.Playlist

class AudioPlayerAdapter(private val onItemClick: (Playlist) -> Unit) :
    RecyclerView.Adapter<AudioPlayerViewHolder>() {
    var listOfPlaylists: List<Playlist> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioPlayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistItemBinding.inflate(inflater, parent, false)
        return AudioPlayerViewHolder(binding)

    }

    override fun onBindViewHolder(
        holder: AudioPlayerViewHolder,
        position: Int
    ) {
        holder.bind(listOfPlaylists[position])
        holder.itemView.setOnClickListener {
            onItemClick(listOfPlaylists[position])
        }
    }

    override fun getItemCount(): Int {
        return listOfPlaylists.size
    }
}