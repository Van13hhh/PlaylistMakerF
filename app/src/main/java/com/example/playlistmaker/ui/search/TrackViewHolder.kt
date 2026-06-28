package com.example.playlistmaker.ui.search

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(
    private val binding: TrackViewBinding,
) : RecyclerView.ViewHolder(binding.root) {

    private val placeholderRes: Int

    init {
        val value = TypedValue()
        binding.root.context.theme.resolveAttribute(R.attr.placeHolderRV, value, true)
        placeholderRes = value.resourceId
    }

    fun bind(track: Track) {
        // Используем binding напрямую — всегда актуально!
        Glide.with(binding.root.context)
            .load(track.artworkUrl100)
            .placeholder(placeholderRes)
            .centerCrop()
            .into(binding.ivTrackImage)

        binding.ivTrackImage.setBackgroundResource(R.drawable.rounded_corners)
        binding.ivTrackImage.setClipToOutline(true)
        binding.tvTrackName.text = track.trackName
        binding.tvAuthorName.text = track.artistName
        binding.trackTime.text = dateFormat.format(track.trackTimeMillis)
    }

    companion object {
        private val dateFormat by lazy {
            SimpleDateFormat("mm:ss", Locale.getDefault())
        }

        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackViewBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }
}