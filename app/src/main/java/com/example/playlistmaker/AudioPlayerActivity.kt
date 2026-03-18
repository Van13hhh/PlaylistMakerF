package com.example.playlistmaker

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.getSerializableExtra("track") as? Track

        track?.let {
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.playlistPlaceHolder, typedValue, true)
            val placeholderRes = typedValue.resourceId
            // Загружаем изображение
            Glide.with(this)
                .load(
                    it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
                )
                .placeholder(placeholderRes)
                .centerCrop()
                .into(binding.ivTrackImage)

            // Устанавливаем тексты
            binding.tvTrackName.text = it.trackName
            binding.tvArtistName.text = it.artistName
            binding.tvTimeTrack.text = formatTime(it.trackTimeMillis)
            binding.tvTime.text = formatTime(it.trackTimeMillis)
            binding.tvGenre.text = it.primaryGenreName
            binding.tvCountry.text = it.country

            if (track.collectionName != null) {
                binding.llAlbumName.isVisible = true
                binding.tvAlbumName.text = it.collectionName
            } else {
                binding.llAlbumName.isVisible = false
            }

            if (track.releaseDate.isNullOrEmpty()) {
                binding.llYear.isVisible = false
            } else {
                binding.llYear.isVisible = true
                binding.tvYear.text = it.releaseDate?.take(4)
            }
        }

        // Кнопка назад
        binding.backToSearch.setOnClickListener {
            finish()
        }
    }

    private fun formatTime(timeMillis: Int): String {
        return try {
            java.text.SimpleDateFormat("mm:ss", java.util.Locale.getDefault())
                .format(timeMillis.toLong())
        } catch (e: Exception) {
            "00:00"
        }
    }
}