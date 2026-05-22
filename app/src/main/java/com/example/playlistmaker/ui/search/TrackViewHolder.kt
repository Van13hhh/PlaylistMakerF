package com.example.playlistmaker.ui.search

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
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
    private val trackImage: ImageView
    private val trackName: TextView
    private val trackAuthorName: TextView
    private val trackTime: TextView
    private val fr_track: FrameLayout
    private val placeholderRes: Int

    init {
        trackImage = itemView.findViewById(R.id.iv_trackImage)
        trackName = itemView.findViewById(R.id.tv_trackName)
        trackAuthorName = itemView.findViewById(R.id.tv_authorName)
        trackTime = itemView.findViewById(R.id.trackTime)
        fr_track = itemView.findViewById(R.id.fr_track)
        val value = TypedValue()
        itemView.context.theme.resolveAttribute(R.attr.placeHolderRV, value, true)
        placeholderRes = value.resourceId
    }

    fun bind(track: Track) {
        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(placeholderRes)
            .centerCrop()
            .into(trackImage)
        trackImage.setBackgroundResource(R.drawable.rounded_corners);
        trackImage.setClipToOutline(true)
        trackName.text = track.trackName
        trackAuthorName.text = track.artistName
        trackTime.text =
            dateFormat.format(track.trackTimeMillis)
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