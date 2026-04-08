package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import kotlinx.coroutines.Runnable
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var url: String
    private lateinit var updateRunnable: Runnable
    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.nestedScrollViewAudioplayer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    binding.tvTimeTrack.text = dateFormat.format(mediaPlayer.currentPosition)
                    handler.postDelayed(this, DELAY_MILLS)
                }
            }
        }

        binding.btnPlay.setOnClickListener {
            playbackControl()
        }
        val track = intent.getSerializableExtra("track") as? Track

        url = track?.previewUrl.toString()

        preparePlayer()

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
        mediaPlayer.setOnCompletionListener {
            binding.tvTimeTrack.text = dateFormat.format(Date(0))
            mediaPlayer.pause()
            binding.btnPlay.setImageResource(R.drawable.button__play_100x100)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks { updateRunnable }
        mediaPlayer.release()
    }

    private fun formatTime(timeMillis: Int): String {
        return try {
            java.text.SimpleDateFormat("mm:ss", java.util.Locale.getDefault())
                .format(timeMillis.toLong())
        } catch (e: Exception) {
            "00:00"
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        binding.btnPlay.setImageResource(R.drawable.button_stop_100x100)
        handler.postDelayed(updateRunnable, 500L)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        binding.btnPlay.setImageResource(R.drawable.button__play_100x100)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY_MILLS = 500L
    }
}