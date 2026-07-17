package com.example.playlistmaker.ui.audioplayer.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.TrackUiModel
import com.example.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AudioPlayerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARGS_TRACK_KEY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARGS_TRACK_KEY)
        }

        if (track == null) {
            findNavController().popBackStack()
            return
        }

        viewModel.loadTrack(track)

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            renderPlayerState(it)
        }

        binding.btnPlay.setOnClickListener {
            viewModel.onPlayButtonClick()
        }

        binding.backToSearch.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivLike.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderTrackInfo(model: TrackUiModel) {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.playlistPlaceHolder, typedValue, true)
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
                renderFavouriteAnimation(state.isFavorite)
            }
            is AudioPlayerViewModel.PlayerState.Track -> {
                renderTrackInfo(state.track)
                renderFavouriteAnimation(state.isFavorite)
            }
        }
    }

    private fun renderFavouriteAnimation(isFavourite: Boolean) {
        if (isFavourite) {
            binding.ivLike.setImageResource(R.drawable.button_red_like_51x51)
        } else {
            binding.ivLike.setImageResource(R.drawable.button_like_51x51)
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

    companion object {
        const val ARGS_TRACK_KEY = "track"
        fun createArgs(track: Track): Bundle =
            Bundle().apply {
                putParcelable(ARGS_TRACK_KEY, track)
            }
    }
}