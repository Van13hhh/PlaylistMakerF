package com.example.playlistmaker.ui.media.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FavoriteTracksFragmentBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.fragments.AudioPlayerFragment
import com.example.playlistmaker.ui.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.search.TrackAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import kotlin.time.Duration.Companion.milliseconds

class FavoriteTracksFragment : Fragment() {
    private var _binding: FavoriteTracksFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavoriteTracksViewModel>()
    private var isClickAllowed = true
    private var searchJob: Job? = null


    private val favoriteTracksAdapter = TrackAdapter(
        clickListener = { track ->
            onTrackClick(track)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoriteTracksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerViews()
        isClickAllowed = true
        searchJob?.cancel()
        viewModel.loadFavouriteTrack()
        viewModel.observeState().observe(viewLifecycleOwner) {
            renderPlayerState(it)
        }
    }

    fun renderPlayerState(state: FavoriteTracksViewModel.FavoriteTrackState) {
        when(state){
            is FavoriteTracksViewModel.FavoriteTrackState.Content -> showFavouriteTracks(state.tracks)
           is FavoriteTracksViewModel.FavoriteTrackState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.apply {
            emptyImgLayout.isVisible = true
            favTracksLayout.isVisible = false
        }
    }

    private fun showFavouriteTracks(tracks: List<Track>) {
        binding.apply {
            emptyImgLayout.isVisible = false
            favTracksLayout.isVisible = true
        }
        favoriteTracksAdapter.updateTracks(tracks)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        searchJob = null
        _binding = null
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(TrackAdapter.CLICK_DEBOUNCE_DELAY.milliseconds)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun setupRecyclerViews() {
        binding.rvSearch.apply {
            adapter = favoriteTracksAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun onTrackClick(track: Track) {
        Log.d("PlayerLog", "🖱️ Клик по треку из избранного: ${track.trackName}, isFavorite = ${track.isFavorite}")
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_mediaFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track)
            )
        }
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}