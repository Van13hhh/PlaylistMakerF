package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(
    itemView: View,
    sharedPreferences: SharedPreferences,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private val trackImage: ImageView
    private val trackName: TextView
    private val trackAuthorName: TextView
    private val trackTime: TextView
    private val fr_track: FrameLayout
    private val searchHistory: SearchHistory

    init {
        trackImage = itemView.findViewById(R.id.iv_trackImage)
        trackName = itemView.findViewById(R.id.tv_trackName)
        trackAuthorName = itemView.findViewById(R.id.tv_authorName)
        trackTime = itemView.findViewById(R.id.trackTime)
        fr_track = itemView.findViewById(R.id.fr_track)
        searchHistory = SearchHistory(sharedPreferences)
    }

    fun bind(model: Track) {
        val value = TypedValue()
        itemView.context.theme.resolveAttribute(R.attr.placeHolderRV, value, true)
        val placeholderRes = value.resourceId
        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .placeholder(placeholderRes)
            .centerCrop()
            .into(trackImage)
        trackImage.setBackgroundResource(R.drawable.rounded_corners);
        trackImage.setClipToOutline(true)
        trackName.text = model.trackName
        trackAuthorName.text = model.artistName
        trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis.toLong())

        fr_track.setOnClickListener {
            searchHistory.addToList(model)
            onTrackClick(model)
        }
    }
}