package com.example.playlistmaker.ui.search.fragments

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.fragments.AudioPlayerFragment
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel.TrackState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var value: String = EMPTY_TEXT
    private var lastSearchQuery = ""
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val viewModel by viewModel<SearchViewModel>()
    private var isReturningFromBackStack = false
    private lateinit var textWatcher: android.text.TextWatcher

    private val searchAdapter = TrackAdapter { track ->
        onTrackClick(track)
    }

    private val historyAdapter = TrackAdapter { track ->
        onTrackClick(track)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isReturningFromBackStack = savedInstanceState != null

        savedInstanceState?.let {
            value = it.getString(USER_TEXT, EMPTY_TEXT)
            lastSearchQuery = it.getString(LAST_SEARCH_QUERY, EMPTY_TEXT)
        }

        setupRecyclerViews()
        setupViewModel()
        setupListeners()

        binding.edTextSearch.removeTextChangedListener(textWatcher)
        binding.edTextSearch.setText(value)
        binding.edTextSearch.setSelection(value.length)
        binding.edTextSearch.addTextChangedListener(textWatcher)

        binding.clearIcon.visibility = clearButtonVisibility(value)

        if (isReturningFromBackStack) {
            binding.edTextSearch.clearFocus()
            binding.root.requestFocus()
        } else {
            showKeyboard()
        }
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true

        updateUiVisibility()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacksAndMessages(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USER_TEXT, value)
        outState.putString(LAST_SEARCH_QUERY, lastSearchQuery)
    }

    private fun setupRecyclerViews() {
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.historyRv.apply {
            adapter = historyAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupViewModel() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun setupListeners() {
        binding.btnError.setOnClickListener {
            hideErrorViews()
            binding.progressBar.isVisible = true
            viewModel.searchDebounce(false, lastSearchQuery)
        }

        textWatcher = binding.edTextSearch.doOnTextChanged { text, _, _, _ ->
            binding.clearIcon.visibility = clearButtonVisibility(text)
            value = text.toString()

            if (!text.isNullOrEmpty()) {
                lastSearchQuery = text.toString()
                viewModel.searchDebounce(true, text.toString())
            } else {
                viewModel.loadHistory()
            }
        }

        binding.clearIcon.setOnClickListener {
            binding.edTextSearch.removeTextChangedListener(textWatcher)
            binding.edTextSearch.setText("")
            binding.edTextSearch.addTextChangedListener(textWatcher)
            updateHistoryVisibility()

            value = EMPTY_TEXT
            lastSearchQuery = EMPTY_TEXT
            binding.progressBar.isVisible = false
            hideErrorViews()
            binding.rvSearch.isVisible = false
            binding.edTextSearch.clearFocus()
            val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(binding.edTextSearch.windowToken, 0)
        }

        binding.historyBtn.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun showKeyboard() {
        binding.edTextSearch.requestFocus()
        binding.edTextSearch.postDelayed({
            val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(binding.edTextSearch, 0)
        }, 200)
    }

    private fun render(state: TrackState) {
        when (state) {
            is TrackState.Loading -> showLoading()
            is TrackState.Content -> showContent(state.tracks)
            is TrackState.Empty -> showError(false, getString(R.string.empty_error))
            is TrackState.Error -> showError(true, getString(R.string.internet_error))
            is TrackState.History -> {
                historyAdapter.updateTracks(state.tracks)
                updateHistoryVisibility()
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            rvSearch.isVisible = false
            LLHistory.isVisible = false
            progressBar.isVisible = true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) {
        binding.apply {
            rvSearch.isVisible = true
            progressBar.isVisible = false
        }
        hideErrorViews()
        searchAdapter.updateTracks(tracks)
    }

    private fun showError(isNetworkError: Boolean, errorMessage: String) {
        binding.apply {
            internetErrorIv.isVisible = isNetworkError
            btnError.isVisible = isNetworkError
            emptyErrorIv.isVisible = !isNetworkError
            tvError.isVisible = true
            tvError.text = errorMessage
            progressBar.isVisible = false
        }
    }

    private fun hideErrorViews() {
        binding.apply {
            btnError.isVisible = false
            tvError.isVisible = false
            emptyErrorIv.isVisible = false
            internetErrorIv.isVisible = false
        }
    }

    private fun updateHistoryVisibility() {
        val hasHistory = historyAdapter.listOfTracks.isNotEmpty()
        binding.LLHistory.visibility = if (hasHistory) View.VISIBLE else View.GONE
    }

    private fun updateUiVisibility() {
        val hasFocus = binding.edTextSearch.hasFocus()
        val textIsEmpty = binding.edTextSearch.text.isNullOrEmpty()

        if (hasFocus && textIsEmpty) {
            updateHistoryVisibility()
            binding.rvSearch.visibility = View.GONE
        } else {
            binding.LLHistory.visibility = View.GONE
            binding.rvSearch.visibility =
                if (searchAdapter.listOfTracks.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(
                { isClickAllowed = true },
                TrackAdapter.CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }

    private fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            viewModel.saveTrackToHistory(track)
            findNavController().navigate(
                R.id.action_searchFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track)
            )
        }
    }

    companion object {
        private const val USER_TEXT = "USER_TEXT"
        private const val LAST_SEARCH_QUERY = "LAST_SEARCH_QUERY"
        private const val EMPTY_TEXT = ""
    }
}