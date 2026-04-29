package com.example.playlistmaker.presentation.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Handler
import android.widget.ProgressBar
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.interactors.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactors.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.audioplayer.AudioPlayerActivity
import com.example.playlistmaker.presentation.search.TrackAdapter.Companion.CLICK_DEBOUNCE_DELAY


class SearchActivity : androidx.appcompat.app.AppCompatActivity() {
    private var value: String = EMPTY_TEXT
    private lateinit var clearBtn: Button
    private lateinit var backButon: Button
    private lateinit var searchField: EditText
    private lateinit var recycler: RecyclerView

    private lateinit var recyclerHistory: RecyclerView
    private lateinit var textViewError: TextView
    private lateinit var buttonError: Button
    private lateinit var imageViewEmptyError: ImageView
    private lateinit var imageViewInternetError: ImageView
    private lateinit var historyFr: FrameLayout
    private lateinit var historyButton: Button
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var linearLayoutHistory: LinearLayout
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private lateinit var trackInteractor: TrackInteractor
    private lateinit var progressBar: ProgressBar
    private var lastSearchQuery = ""
    private val handler = Handler(Looper.getMainLooper())
    private var listOfTracks: MutableList<Track> = mutableListOf()
    private var isActivityAlive = false
    private var isClickAllowed = true

    private val searchRunnable = Runnable {
        val query = searchField.text.toString()
        if (query.isNotEmpty()) {
            searchTrack(query)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isActivityAlive = true

        enableEdgeToEdge()
        setContentView(com.example.playlistmaker.R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(com.example.playlistmaker.R.id.ll_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        clearBtn = findViewById(com.example.playlistmaker.R.id.clearIcon)
        backButon = findViewById(com.example.playlistmaker.R.id.back_to_main_menu_search)
        searchField = findViewById(com.example.playlistmaker.R.id.ed_text_search)
        recycler = findViewById(com.example.playlistmaker.R.id.rv_search)
        recyclerHistory = findViewById(com.example.playlistmaker.R.id.history_rv)
        textViewError = findViewById(com.example.playlistmaker.R.id.tv_error)
        buttonError = findViewById(com.example.playlistmaker.R.id.btn_error)
        imageViewEmptyError = findViewById(com.example.playlistmaker.R.id.empty_error_iv)
        imageViewInternetError = findViewById(com.example.playlistmaker.R.id.internet_error_iv)
        historyFr = findViewById(com.example.playlistmaker.R.id.history_fr)
        historyButton = findViewById(com.example.playlistmaker.R.id.history_btn)
        linearLayoutHistory = findViewById(com.example.playlistmaker.R.id.LL_history)
        progressBar = findViewById(com.example.playlistmaker.R.id.progressBar)

        searchHistoryInteractor = Creator.getSearchHistoryInteractor(this)
        trackInteractor = Creator.provideTrackInteractor()

        recycler.adapter = TrackAdapter(
            listOfTracks
        ) { track ->
            onTrackClick(track)
        }

        recycler.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        historyAdapter =
            TrackAdapter(
                searchHistoryInteractor.getHistory()
            ) { track ->
                onTrackClick(track)
            }

        recyclerHistory.adapter = historyAdapter
        recyclerHistory.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        searchField.setText(value)
        clearBtn.visibility = clearButtonVisability(value)
        searchField.requestFocus()
        searchField.postDelayed({
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT)
        }, 200)

        backButon.setOnClickListener {
            finish()
        }

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val userSearch = searchField.text.toString()
                if (userSearch.isNotEmpty()) {
                    searchTrack(userSearch)
                    return@setOnEditorActionListener false
                } else {
                    return@setOnEditorActionListener true
                }
            }
            false
        }
        buttonError.setOnClickListener {
            hideErrorViews()
            searchTrack(lastSearchQuery)
        }

        searchField.setOnFocusChangeListener { _, _ ->
            updateUiVisibility()
        }

        searchField.doOnTextChanged { text, _, _, _ ->
            updateUiVisibility()
            if (!text.isNullOrEmpty())
                clearBtn.visibility = clearButtonVisability(text)
            else
                clearBtn.visibility = View.GONE
            value = searchField.text.toString()
            searchDebounce()
        }



        showHistoryUi()

        clearBtn.setOnClickListener {
            searchField.setText("")
            progressBar.isVisible = false
            hideErrorViews()
            recycler.isVisible = false
            listOfTracks.clear()
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchField.windowToken, 0)
            updateHistoryAdapter()
            showHistoryUi()
        }

        historyButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            hideHistoryUi()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityAlive = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clearButtonVisability(s: CharSequence?): Int {
        return if (s.isNullOrEmpty())
            View.GONE
        else
            View.VISIBLE
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        value = savedInstanceState.getString(USER_TEXT, EMPTY_TEXT)

        searchField.setText(value)
        clearBtn.visibility = clearButtonVisability(value)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USER_TEXT, value)
    }

    companion object {
        private const val USER_TEXT = "USER_TEXT"
        private const val EMPTY_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun searchTrack(text: String) {
        lastSearchQuery = text
        hideErrorViews()
        progressBar.isVisible = true

        trackInteractor.searchTrack(text, object : TrackInteractor.TrackConsumer {
            @SuppressLint("NotifyDataSetChanged")
            override fun consume(foundTracks: List<Track>, resultCode: Int) {
                runOnUiThread {
                    progressBar.isVisible = false

                    if (resultCode == -1) {
                        showInternetError()
                    } else if (foundTracks.isEmpty()) {
                        showEmptyState()
                    } else {
                        hideErrorViews()
                        listOfTracks.clear()
                        listOfTracks.addAll(foundTracks)
                        recycler.isVisible = true
                        recycler.adapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }

     private fun hideErrorViews() {
        buttonError.isVisible = false
        textViewError.isVisible = false
        imageViewEmptyError.isVisible = false
         imageViewInternetError.isVisible = false
    }
    private fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            searchHistoryInteractor.saveTrack(track)
            updateHistoryAdapter()
            changeActivityToAP(track)
        }
    }
    private fun clickDebounce(): Boolean {
        val handler = handler
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun showEmptyState() {
        textViewError.isVisible = true
        textViewError.text = getString(com.example.playlistmaker.R.string.empty_error)
        imageViewEmptyError.isVisible = true
        imageViewInternetError.isVisible = false
    }

    fun showInternetError() {
        textViewError.isVisible = true
        textViewError.text = getString(com.example.playlistmaker.R.string.internet_error)
        buttonError.visibility = View.VISIBLE
        recycler.visibility = View.GONE
        imageViewEmptyError.isVisible = false
        imageViewInternetError.isVisible = true

    }
    fun showHistoryUi() {
        if (searchHistoryInteractor.getHistory().isNotEmpty()) {
            linearLayoutHistory.visibility = View.VISIBLE
        } else {
            linearLayoutHistory.visibility = View.GONE
        }
    }

    fun hideHistoryUi() {
        linearLayoutHistory.visibility = View.GONE
    }

    private fun updateHistoryAdapter() {
        val historyList = searchHistoryInteractor.getHistory()
        historyAdapter.updateTracks(historyList)
    }


    private fun changeActivityToAP(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    private fun updateUiVisibility() {
        val hasFocus = searchField.hasFocus()
        val textIsEmpty = searchField.text.isNullOrEmpty()

        if (hasFocus && textIsEmpty) {
            showHistoryUi()
            recycler.visibility = View.GONE
        } else {
            hideHistoryUi()
            if (listOfTracks.isNotEmpty()) {
                recycler.visibility = View.VISIBLE
            }
        }
    }
}