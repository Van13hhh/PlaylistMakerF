package com.example.playlistmaker.ui.media.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentListOfPlaylistsBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.media.PlaylistAdapter
import com.example.playlistmaker.ui.media.view_model.ListOfPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListOfPlaylistsFragment : Fragment() {
    private var _binding: FragmentListOfPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListOfPlaylistsViewModel by viewModel()

    private val playlistAdapter = PlaylistAdapter { playlist ->
        val bundle = Bundle().apply {
            putLong(ARG_PLAYLIST_ID, playlist.id)
        }
        findNavController().navigate(
            R.id.action_mediaFragment_to_playlistFragment,
            bundle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListOfPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistRecycleView.apply {
            adapter = playlistAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        viewModel.updateUi()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
        binding.btnCreatePlaylist.setOnClickListener {
            findNavController().navigate(R.id.playlistCreatingFragment)
        }
    }

    private fun renderState(state: ListOfPlaylistsViewModel.PlaylistState) {
        when (state) {
            is ListOfPlaylistsViewModel.PlaylistState.Content -> showContent(state.playlists)
            is ListOfPlaylistsViewModel.PlaylistState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.playlistRecycleView.isVisible = false
        binding.emptyPlaylistsError.isVisible = true

    }

    private fun showContent(playlists: List<Playlist>) {
        binding.playlistRecycleView.isVisible = true
        binding.emptyPlaylistsError.isVisible = false
        playlistAdapter.listOfPlaylists = playlists
        playlistAdapter.notifyDataSetChanged()
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlist_id"
        fun newInstance(): ListOfPlaylistsFragment = ListOfPlaylistsFragment()
    }
}