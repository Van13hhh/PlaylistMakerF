package com.example.playlistmaker.ui.audioplayer.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.audioplayer.activity.TrackUiModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel by viewModel<AudioPlayerViewModel>()

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

        if (track == null) {
            finish()
            return
        }

        viewModel.loadTrack(track)


        viewModel.observePlayerState().observe(this) {
            renderPlayerState(it)
        }

        binding.btnPlay.setOnClickListener {
            viewModel.onPlayButtonClick()
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
    private fun renderPlayerState(state: AudioPlayerViewModel.PlayerState) {
        when (state) {
            is AudioPlayerViewModel.PlayerState.PlayingState -> {
                renderPlayingState(state.state)
                binding.tvTimeTrack.text = state.time
            }

            is AudioPlayerViewModel.PlayerState.Track -> renderTrackInfo(state.track)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun renderPlayingState(state: Int) {
        binding.btnPlay.setImageResource(
            when (state) {
                AudioPlayerViewModel.STATE_PLAYING -> R.drawable.button_stop_100x100
                else -> R.drawable.button__play_100x100
            }
        )
    }

}