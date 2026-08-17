package com.example.playlistmaker.ui.playlist.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.creating_playlist.fragments.PlaylistCreateFragment
import com.example.playlistmaker.ui.playlist.view_model.PlaylistEditState
import com.example.playlistmaker.ui.playlist.view_model.PlaylistEditViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistEditFragment : PlaylistCreateFragment() {
    private var playlist: Playlist? = null

    override val viewModel: PlaylistEditViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = getPlaylist()
        playlist?.let { viewModel.initPlaylist(it) }

        observeState()
        setupEditMode()
        backButtonClick(null)
    }

    private fun getPlaylist(): Playlist? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("playlist", Playlist::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("playlist")
        }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is PlaylistEditState.Content -> {
                    playlist = state.playlist
                    loadPlaylist()
                }
            }
        })
    }

    private fun setupEditMode() {
        binding.btnCreate.text = "Сохранить"
        binding.textView.text = "Редактировать плейлист"
    }

    private fun loadPlaylist() {
        binding.playlistName.setText(playlist?.name ?: "")
        binding.playlistDescription.setText(playlist?.description ?: "")

        val photoUriString = playlist?.photoUri
        if (photoUriString != null) {
            binding.playlistImageButton.isVisible = false
            binding.playlistImageView.apply {
                isVisible = true
                setImageURI(photoUriString.toString().toUri())
            }
        } else {
            binding.playlistImageButton.isVisible = true
            binding.playlistImageView.isVisible = false
        }
    }

    override fun backButtonClick(confirmDialog: AlertDialog?) {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun createPlaylist() {
        val name = binding.playlistName.text.toString().trim()
        val description = binding.playlistDescription.text.toString().trim()

        val photoPath = photoUri?.let { saveImageToPrivateStorage(it) }
            ?: playlist?.photoUri

        val updatedPlaylist = Playlist(
            id = playlist?.id ?: 0,
            name = name,
            description = description.takeIf { it.isNotEmpty() },
            photoUri = photoPath.toString().toUri(),
            listOfTrackIds = playlist?.listOfTrackIds ?: mutableListOf(),
            countTracks = playlist?.countTracks ?: 0
        )

        viewModel.addPlaylist(updatedPlaylist)
        findNavController().popBackStack()
    }
}