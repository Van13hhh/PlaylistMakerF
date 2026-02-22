package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val trackImage: ImageView
    private val trackName: TextView
    private val trackAuthorName: TextView
    private val trackTime: TextView

    init {
        trackImage = itemView.findViewById(R.id.iv_trackImage)
        trackName = itemView.findViewById(R.id.tv_trackName)
        trackAuthorName = itemView.findViewById(R.id.tv_authorName)
        trackTime = itemView.findViewById(R.id.trackTime)
    }
    fun bind(model: Track){
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.outline_android_wifi_3_bar_off_24)
            .centerCrop()
            .into(trackImage)
        trackImage.setBackgroundResource(R.drawable.rounded_corners);
        trackImage.setClipToOutline(true)

        trackName.text = model.trackName
        trackAuthorName.text = model.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis.toLong())
    }
}