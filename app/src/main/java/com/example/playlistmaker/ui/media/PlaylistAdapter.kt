package com.example.playlistmaker.ui.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.domain.playlist.model.Playlist

class PlaylistAdapter(val onClick: (playlist: Playlist) -> Unit) :
    RecyclerView.Adapter<PlaylistViewHolder>() {
    var listOfPlaylists: List<Playlist> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistViewBinding.inflate(inflater, parent, false)
        return PlaylistViewHolder(binding)

    }


    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int
    ) {
        holder.bind(listOfPlaylists[position])
        holder.itemView.setOnClickListener {
            onClick(listOfPlaylists[position])
        }
    }

    override fun getItemCount(): Int {
        return listOfPlaylists.size
    }
}