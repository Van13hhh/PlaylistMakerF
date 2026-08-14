package com.example.playlistmaker.ui.media

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import java.io.File

class PlaylistViewHolder(private val binding: PlaylistViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        val photoUri = playlist.photoUri
        if (photoUri != null) {
            Glide.with(binding.root.context)
                .load(File(photoUri.toString()))
                .placeholder(R.drawable.placeholder_45x45)
                .centerCrop()
                .into(binding.ivPlaylistPhoto)
        } else {
            binding.ivPlaylistPhoto.setImageResource(R.drawable.placeholder_45x45)
        }
        binding.tvPlaylistName.text = playlist.name
        binding.tvPlaylistCount.text = binding.root.context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.countTracks,
            playlist.countTracks
        )
    }
}