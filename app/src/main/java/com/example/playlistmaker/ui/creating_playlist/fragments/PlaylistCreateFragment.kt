package com.example.playlistmaker.ui.creating_playlist.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreatingBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.creating_playlist.view_model.PlaylistCreateViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class PlaylistCreateFragment : Fragment() {
    private var _binding: FragmentPlaylistCreatingBinding? = null
    private val binding get() = _binding!!
    private var photoUri: Uri? = null

    private val viewModel: PlaylistCreateViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistCreatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    showPic(uri)
                    photoUri = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.playlistImageButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playlistName.doAfterTextChanged { text ->
            binding.btnCreate.isEnabled = text?.isNotEmpty() == true
        }

        val confirmDialog = MaterialAlertDialogBuilder(
            requireContext(),
            androidx.appcompat.R.attr.colorPrimary
        )
            .setTitle(R.string.end_creating_playlist)
            .setMessage(R.string.cancel_save)
            .setNeutralButton(R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                findNavController().popBackStack()
            }

        binding.backBtn.setOnClickListener {
            if (hasUnsavedChanges()) {
                confirmDialog.show()
            } else {
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (hasUnsavedChanges()) {
                confirmDialog.show()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.btnCreate.setOnClickListener {
            createPlaylist()
        }
    }

    private fun showPic(uri: Uri) {
        binding.playlistImageButton.isVisible = false
        binding.playlistImageView.apply {
            isVisible = true
            setImageURI(uri)
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        return binding.playlistName.text?.isNotEmpty() == true ||
                binding.playlistDescription.text?.isNotEmpty() == true ||
                photoUri != null
    }

    private fun createPlaylist() {
        val name = binding.playlistName.text.toString().trim()
        val description = binding.playlistDescription.text.toString().trim()

        val photoPath = photoUri?.let { saveImageToPrivateStorage(it) }
        Log.d("PlaylistCreate", "photoPath: $photoPath")

        val playlist = Playlist(
            name = name,
            description = description.takeIf { it.isNotEmpty() },
            photoUri = photoPath?.toUri(),
            listOfTrackIds = mutableListOf(),
            countTracks = 0
        )

        viewModel.addPlaylist(playlist)

        Toast.makeText(requireContext(), "Плейлист \"$name\" создан", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun saveImageToPrivateStorage(uri: Uri): String? {
        return try {
            val filePath = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "myalbum"
            )
            if (!filePath.exists()) {
                filePath.mkdirs()
            }

            val timestamp = System.currentTimeMillis()
            val fileName = "cover_$timestamp.jpg"
            val file = File(filePath, fileName)

            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }

            Log.d("PlaylistCreate", "File saved: ${file.absolutePath}")
            file.absolutePath

        } catch (e: Exception) {
            Log.e("PlaylistCreate", "Error saving image", e)
            null
        }
    }
}