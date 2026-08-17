package com.example.playlistmaker.ui.playlist.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.TrackUiModel
import com.example.playlistmaker.ui.audioplayer.fragments.AudioPlayerFragment
import com.example.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.util.converters.TrackConverter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
import com.google.android.material.R as MaterialR

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()

    private var playlist: Playlist? = null

    private var playlistBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var standardBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private val playlistId: Long by lazy {
        arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L
    }

    private var isClickAllowed = true

    private val trackConverter = TrackConverter()

    private var searchJob: Job? = null

    private val playlistAdapter = TrackAdapter(
        clickListener = { track ->
            onTrackClick(track)
        },
        longClickListener = object : TrackAdapter.LongClickListener {
            override fun onTrackLongClick(track: Track): Boolean {
                showDeleteTrackDialog(track)
                return true
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet)
        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)

        playlistBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        standardBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        if (playlistId != 0L) {
            viewModel.loadPlaylist(playlistId)
        } else {
            findNavController().popBackStack()
        }

        binding.backToListOfPlaylists.setOnClickListener {
            findNavController().popBackStack()
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.observeEvents().observe(viewLifecycleOwner) { event ->
            renderEvent(event)
        }
    }

    private fun setupClickListeners() {
        binding.btnShare.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.btnShareBottomSheet.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.btnEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable(ARG_PLAYLIST, playlist)
            }
            findNavController().navigate(
                R.id.action_playlistFragment_to_playlistEditFragment,
                bundle
            )
        }

        binding.btnDelete.setOnClickListener {
            showDeletePlaylistDialog()
        }

        binding.btnMore.setOnClickListener {
            viewModel.onMoreClick()
        }
    }

    private fun renderState(state: PlaylistViewModel.PlaylistState) {
        when (state) {
            is PlaylistViewModel.PlaylistState.Content -> renderPlaylist(
                state.tracks,
                state.playlist
            )
        }
    }

    private fun renderEvent(event: PlaylistViewModel.PlaylistEvent) {
        when (event) {
            is PlaylistViewModel.PlaylistEvent.SharePlaylist -> {
                playlist = event.playlist
                renderShareScreen(
                    event.playlist,
                    event.tracks
                )
            }

            is PlaylistViewModel.PlaylistEvent.ShowToast -> renderEmptyToast(event.message)
            is PlaylistViewModel.PlaylistEvent.MorePlaylist -> renderMoreScreen(event.playlist)
        }
    }

    private fun renderEmptyToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun renderShareScreen(playlist: Playlist, tracks: List<TrackUiModel>) {
        val message = buildShareMessage(playlist, tracks)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(Intent.createChooser(intent, "Поделиться плейлистом"))
    }

    private fun renderMoreScreen(playlist: Playlist) {
        binding.standardBottomSheet.isVisible = false
        binding.playlistBottomSheet.isVisible = true
        playlistBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.overlay.isVisible = true

        Glide.with(this)
            .load(playlist.photoUri)
            .placeholder(R.drawable.album_1)
            .centerCrop()
            .into(binding.ivPlaylistPhoto)

        binding.tvPlaylistName.text = playlist.name
        binding.tvPlaylisCount.text = resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.listOfTrackIds.size,
            playlist.listOfTrackIds.size
        )

        binding.overlay.setOnClickListener {
            hidePlaylistBottomSheet()
        }

        playlistBottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hidePlaylistBottomSheet()
                }
            }

            override fun onSlide(p0: View, p1: Float) {
            }
        })
    }

    private fun hidePlaylistBottomSheet() {
        binding.standardBottomSheet.isVisible = true
        binding.playlistBottomSheet.isVisible = false
        binding.overlay.isVisible = false
    }

    private fun buildShareMessage(playlist: Playlist, tracks: List<TrackUiModel>): String {
        val trackList = tracks.mapIndexed { index, track ->
            val millis = track.trackTimeMillis.toLong()
            val minutes = millis / 1000 / 60
            val seconds = millis / 1000 % 60
            val duration = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
            "${index + 1}. ${track.artistName} - ${track.trackName} ($duration)"
        }.joinToString("\n")

        val tracksCount = resources.getQuantityString(
            R.plurals.tracks_count,
            tracks.size,
            tracks.size
        )

        return "${playlist.name}\n${playlist.description ?: ""}\n$tracksCount\n$trackList"
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderPlaylist(tracks: List<TrackUiModel>, playlist: Playlist) {
        this.playlist = playlist
        Glide.with(this)
            .load(File(playlist.photoUri.toString()))
            .placeholder(R.drawable.album_1)
            .centerCrop()
            .into(binding.playlistImage)

        binding.playlistNameTv.text = playlist.name
        binding.playlistDescriptionTv.text = playlist.description
        binding.playlistCountTracksTv.text = resources.getQuantityString(
            R.plurals.tracks_count,
            tracks.size,
            tracks.size
        )

        val totalMinutes = calculateTotalDuration(tracks)
        binding.playlistTimeTv.text = resources.getQuantityString(
            R.plurals.minutes_count,
            totalMinutes,
            totalMinutes
        )

        binding.rvPlaylistTracks.apply {
            adapter = playlistAdapter
            layoutManager = LinearLayoutManager(requireContext())
            playlistAdapter.updateTracks(tracks.map { trackUiModel ->
                trackConverter.convert(trackUiModel)
            })
            adapter?.notifyDataSetChanged()
        }
    }

    private fun calculateTotalDuration(tracks: List<TrackUiModel>): Int {
        val totalMillis = tracks.sumOf { it.trackTimeMillis.toLong() }
        return SimpleDateFormat("mm", Locale.getDefault()).format(totalMillis).toInt()
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

    private fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_playlistFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track)
            )
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить трэк?")
            .setPositiveButton("Да") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteTrack(track.trackId.toLong(), playlistId)
                }
            }
            .setNegativeButton("Нет", null)
            .create()

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawable(Color.WHITE.toDrawable())

            val blueColor = ContextCompat.getColor(requireContext(), R.color.YPBlue)
            val whiteBackground = Color.WHITE.toDrawable()

            dialog.findViewById<TextView>(MaterialR.id.alertTitle)?.apply {
                setTextColor(Color.BLACK)
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                setTextColor(blueColor)
                background = whiteBackground
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
                setTextColor(blueColor)
                background = whiteBackground
            }
        }

        dialog.show()
    }

    private fun showDeletePlaylistDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить плейлист \"${playlist?.name ?: ""}\" ?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.deletePlaylist()
                findNavController().popBackStack()
                playlist = null
            }
            .setNegativeButton("Нет", null)
            .create()

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawable(Color.WHITE.toDrawable())

            val blueColor = ContextCompat.getColor(requireContext(), R.color.YPBlue)
            val whiteBackground = Color.WHITE.toDrawable()

            dialog.findViewById<TextView>(MaterialR.id.alertTitle)?.apply {
                setTextColor(Color.BLACK)
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                setTextColor(blueColor)
                background = whiteBackground
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
                setTextColor(blueColor)
                background = whiteBackground
            }
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PLAYLIST = "playlist"
        const val ARG_PLAYLIST_ID = "playlist_id"
    }
}