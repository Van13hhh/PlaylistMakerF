package com.example.playlistmaker.presentation.audioplayer

import android.annotation.SuppressLint
import android.os.Build
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
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.domain.mapers.DateTimeFormater
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.TrackUiModel
import kotlinx.coroutines.Runnable

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val playerInteractor = Creator.provideAudioPlayerInteractor()
    private val trackConverter = Creator.provideTrackConverter()
    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTime = playerInteractor.getCurrentPosition()
            binding.tvTimeTrack.text = DateTimeFormater.format(currentTime)
            handler.postDelayed(this, DELAY_MILLS)
        }
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
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("track")
        }

        if (track != null) {
            val trackUiModel = trackConverter.convert(track)

            val url = trackUiModel.previewUrl
            renderTrackInfo(trackUiModel)

            playerInteractor.prepare(url) { state ->
                renderPlayerState(state)
            }
        } else {
            finish()
        }

        binding.btnPlay.setOnClickListener {
            playbackControl()
        }

        binding.backToSearch.setOnClickListener {
            finish()
        }
    }

    private fun renderTrackInfo(model: TrackUiModel) {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.playlistPlaceHolder, typedValue, true)
        val imagePlaceholder = typedValue.resourceId

        binding.tvTrackName.text = model.trackName
        binding.tvArtistName.text = model.artistName
        binding.tvTime.text = model.trackTimeMillis
        binding.tvGenre.text = model.primaryGenreName
        binding.tvCountry.text = model.country
        binding.tvYear.text = model.releaseDate

        if (model.collectionName.isNullOrEmpty()) {
            binding.llAlbumName.isVisible = false
        } else {
            binding.llAlbumName.isVisible = true
            binding.tvAlbumName.text = model.collectionName
        }

        Glide.with(this)
            .load(model.artworkUrl100)
            .placeholder(imagePlaceholder)
            .centerCrop()
            .into(binding.ivTrackImage)
    }

    @SuppressLint("SetTextI18n")
    private fun renderPlayerState(state: AudioPlayerInteractor.PlayerState) {
        when (state) {
            AudioPlayerInteractor.PlayerState.STATE_PLAYING -> {
                binding.btnPlay.setImageResource(R.drawable.button_stop_100x100)
                startTimer()
            }

            AudioPlayerInteractor.PlayerState.STATE_PAUSED -> {
                binding.btnPlay.setImageResource(R.drawable.button__play_100x100)
                stopTimer()
            }

            AudioPlayerInteractor.PlayerState.STATE_PREPARED -> {
                binding.btnPlay.setImageResource(R.drawable.button__play_100x100)
                stopTimer()
                binding.tvTimeTrack.text = "00:00"
            }

            else -> Unit
        }
    }

    private fun playbackControl() {
        if (playerInteractor.isPlaying()) {
            playerInteractor.pause()
        } else {
            playerInteractor.start()
        }
    }

    private fun startTimer() {
        handler.post(updateRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        playerInteractor.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        playerInteractor.release()
    }

    companion object {
        private const val DELAY_MILLS = 500L
    }
}