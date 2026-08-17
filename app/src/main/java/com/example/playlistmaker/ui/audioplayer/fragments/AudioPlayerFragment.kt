package com.example.playlistmaker.ui.audioplayer.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.AudioPlayerAdapter
import com.example.playlistmaker.ui.audioplayer.TrackUiModel
import com.example.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AudioPlayerViewModel>()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null
    private var currentTrack: Track? = null

    private val audioPlayerAdapter = AudioPlayerAdapter { playlist ->
        viewModel.onPlaylistClick(playlist, currentTrack)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTrack()
        setupBottomSheet()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        updateOverlayState()
    }

    private fun setupTrack() {
        currentTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARGS_TRACK_KEY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARGS_TRACK_KEY)
        }

        if (currentTrack == null) {
            findNavController().popBackStack()
            return
        }

        viewModel.loadTrack(currentTrack!!)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            peekHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()
        }

        bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (_binding == null) return
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.apply {
                            visibility = View.GONE
                            alpha = 0f
                        }
                    }
                    else -> {
                        binding.overlay.apply {
                            visibility = View.VISIBLE
                            alpha = 1f
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (_binding == null) return
                binding.overlay.alpha = if (slideOffset >= 0) slideOffset else 0f
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback!!)
    }

    private fun setupRecyclerView() {
        binding.rvPlaylists.apply {
            adapter = audioPlayerAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        binding.btnPlay.setOnClickListener {
            viewModel.onPlayButtonClick()
        }

        binding.backToSearch.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivLike.setOnClickListener {
            viewModel.onFavoriteClicked(currentTrack)
        }

        binding.ivAddToPlaylist.setOnClickListener {
            viewModel.getPlaylists()
        }

        binding.btnCreatePlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_playlistCreatingFragment)
        }
    }

    private fun setupObservers() {
        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            renderPlayerState(state)
        }

        viewModel.showBottomSheet.observe(viewLifecycleOwner) { playlists ->
            showBottomSheet(playlists)
        }

        viewModel.addTrackStatus.observe(viewLifecycleOwner) { status ->
            renderAddTrackStatus(status)
        }
    }

    private fun updateOverlayState() {
        if (::bottomSheetBehavior.isInitialized) {
            val currentState = bottomSheetBehavior.state
            if (currentState == BottomSheetBehavior.STATE_HIDDEN) {
                binding.overlay.apply {
                    isInvisible = true
                    alpha = 0f
                }
            } else {
                binding.overlay.apply {
                    isInvisible = false
                    alpha = 1f
                }
            }
        }
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
                binding.btnPlay.isEnabled = true
            }
            is AudioPlayerViewModel.PlayerState.Track -> {
                renderTrackInfo(state.track)
                renderFavouriteAnimation(state.isFavorite)
            }
        }
    }

    private fun showBottomSheet(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            binding.rvPlaylists.visibility = View.GONE
            binding.llEmptyPlaylists.visibility = View.VISIBLE
        } else {
            binding.rvPlaylists.visibility = View.VISIBLE
            binding.llEmptyPlaylists.visibility = View.GONE
            audioPlayerAdapter.listOfPlaylists = playlists
            audioPlayerAdapter.notifyDataSetChanged()
        }

        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun renderAddTrackStatus(status: AudioPlayerViewModel.AddTrackStatus) {
        when (status) {
            is AudioPlayerViewModel.AddTrackStatus.Success -> {
                Toast.makeText(
                    requireContext(),
                    "Трек добавлен в плейлист ${status.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
                hideBottomSheet()
            }

            is AudioPlayerViewModel.AddTrackStatus.AlreadyExists -> {
                Toast.makeText(
                    requireContext(),
                    "Трек уже добавлен в плейлист ${status.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun hideBottomSheet() {
        if (!::bottomSheetBehavior.isInitialized) return
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.root.postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay.isInvisible = true
            binding.overlay.alpha = 0f
        }, 100)
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

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetCallback?.let { callback ->
            if (::bottomSheetBehavior.isInitialized) {
                bottomSheetBehavior.removeBottomSheetCallback(callback)
            }
        }
        bottomSheetCallback = null
        _binding = null
    }

    companion object {
        const val ARGS_TRACK_KEY = "track"
        fun createArgs(track: Track): Bundle =
            Bundle().apply {
                putParcelable(ARGS_TRACK_KEY, track)
            }
    }
}