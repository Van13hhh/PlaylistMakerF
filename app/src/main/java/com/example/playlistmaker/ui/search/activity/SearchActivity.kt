package com.example.playlistmaker.ui.search.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.activity.AudioPlayerActivity
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel.TrackState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private var value: String = EMPTY_TEXT
    private lateinit var binding: ActivitySearchBinding
    private var lastSearchQuery = ""
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private  val viewModel by viewModel<SearchViewModel>()

    private val searchAdapter = TrackAdapter { track ->
        onTrackClick(track)
    }

    private val historyAdapter = TrackAdapter { track ->
        onTrackClick(track)
    }

    // ==================== ЖИЗНЕННЫЙ ЦИКЛ ====================

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.llSearch) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerViews()
        setupViewModel()
        setupListeners()
        setupInitialState()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USER_TEXT, value)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        value = savedInstanceState.getString(USER_TEXT, EMPTY_TEXT)
        binding.edTextSearch.setText(value)
        binding.clearIcon.visibility = clearButtonVisibility(value)
    }

    // ==================== ИНИЦИАЛИЗАЦИЯ ====================

    private fun setupRecyclerViews() {
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        }
        binding.historyRv.apply {
            adapter = historyAdapter
            layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupViewModel() {
        viewModel.observeState().observe(this) { state ->
            render(state)
        }
    }

    private fun setupListeners() {
        binding.backToMainMenuSearch.setOnClickListener {
            finish()
        }

        binding.btnError.setOnClickListener {
            hideErrorViews()
            binding.progressBar.isVisible = true
            viewModel.searchDebounce(false, lastSearchQuery)
        }

        binding.edTextSearch.setOnFocusChangeListener { _, _ ->
            updateUiVisibility()
        }

        binding.edTextSearch.doOnTextChanged { text, _, _, _ ->
            updateUiVisibility()
            binding.clearIcon.visibility = clearButtonVisibility(text)
            value = text.toString()

            if (!text.isNullOrEmpty()) {
                lastSearchQuery = text.toString()
                viewModel.searchDebounce(true, text.toString())
            }
        }

        binding.clearIcon.setOnClickListener {
            binding.edTextSearch.setText("")
            binding.progressBar.isVisible = false
            hideErrorViews()
            binding.rvSearch.isVisible = false
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(binding.edTextSearch.windowToken, 0)
        }

        binding.historyBtn.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun setupInitialState() {
        binding.edTextSearch.setText(value)
        binding.clearIcon.visibility = clearButtonVisibility(value)
        binding.edTextSearch.requestFocus()
        binding.edTextSearch.postDelayed({
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edTextSearch, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    // ==================== UI ОБНОВЛЕНИЯ ====================

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

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

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
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)

            viewModel.saveTrackToHistory(track)
        }
    }

    companion object {
        private const val USER_TEXT = "USER_TEXT"
        private const val EMPTY_TEXT = ""
    }
}