package com.example.playlistmaker.ui.audioplayer

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import java.io.File

class AudioPlayerViewHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(playlist: Playlist) {
        val photoPath = playlist.photoPath
        if (!photoPath.isNullOrEmpty()) {
            Glide.with(binding.root.context)
                .load(File(photoPath))
                .placeholder(R.drawable.placeholder_45x45)
                .centerCrop()
                .into(binding.ivPlaylistPhoto)
        } else {
            binding.ivPlaylistPhoto.setImageResource(R.drawable.placeholder_45x45)
        }
        binding.tvPlaylistName.text = playlist.name
        binding.tvPlaylistCount.text =
            "${playlist.countTracks} ${getTracksWord(playlist.countTracks)}"

    }

    private fun getTracksWord(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "трек"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "трека"
            else -> "треков"
        }
    }
}