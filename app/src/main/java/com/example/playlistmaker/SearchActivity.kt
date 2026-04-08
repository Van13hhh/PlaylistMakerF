package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Looper
import android.util.Log
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.os.Handler
import android.widget.ProgressBar

const val HISTORY_TRACK_KEY = "history_key"

class SearchActivity : AppCompatActivity() {
    private var value: String = EMPTY_TEXT
    private lateinit var clearBtn: Button
    private lateinit var backButon: Button
    private lateinit var searchField: EditText
    private lateinit var recycler: RecyclerView

    private lateinit var recyclerHistory: RecyclerView
    private lateinit var textViewError: TextView
    private lateinit var buttonError: Button
    private lateinit var imageViewError: ImageView
    private lateinit var historyFr: FrameLayout
    private lateinit var historyButton: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var linearLayoutHistory: LinearLayout
    private lateinit var progressBar: ProgressBar
    private var isNightMode = false
    private var lastSearchQuery = ""
    private val handler = Handler(Looper.getMainLooper())
    private var listOfTracks: MutableList<Track> = mutableListOf()

    private var currentCall: Call<TrackResponse>? = null

    private val searchRunnable = Runnable {
        val query = searchField.text.toString()
        if (query.isNotEmpty()) {
            searchTrack(query)
        }
    }
    lateinit var track: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ll_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        isNightMode = (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

        clearBtn = findViewById(R.id.clearIcon)
        backButon = findViewById(R.id.back_to_main_menu_search)
        searchField = findViewById(R.id.ed_text_search)
        recycler = findViewById(R.id.rv_search)
        recyclerHistory = findViewById(R.id.history_rv)
        textViewError = findViewById(R.id.tv_error)
        buttonError = findViewById(R.id.btn_error)
        imageViewError = findViewById(R.id.iv_error)
        historyFr = findViewById(R.id.history_fr)
        historyButton = findViewById(R.id.history_btn)
        linearLayoutHistory = findViewById(R.id.LL_history)
        listOfTracks = mutableListOf()
        searchHistory = SearchHistory(getSharedPreferences(HISTORY_TRACK_KEY, MODE_PRIVATE))
        sharedPreferences = getSharedPreferences(HISTORY_TRACK_KEY, MODE_PRIVATE)
        progressBar = findViewById(R.id.progressBar)

        recycler.adapter = TrackAdapter(
            listOfTracks, sharedPreferences,
            { track ->
                updateHistoryAdapter()
                showHistoryUi()
                changeActivityToAP(track)
            },
        )

        recycler.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        searchField.setText(value)
        clearBtn.visibility = clearButtonVisability(value)
        searchField.requestFocus()
        searchField.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT)
        }, 200)

        backButon.setOnClickListener {
            finish()
        }

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                track = searchField.text.toString()
                searchTrack(track)
            }
            false
        }
        buttonError.setOnClickListener {
            hideErrorViews()
            searchTrack(lastSearchQuery)
        }

        searchField.setOnFocusChangeListener { _, hasFocus ->
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

        historyAdapter =
            TrackAdapter(searchHistory.getFromSharedPreference(), sharedPreferences, { track ->
                changeActivityToAP(track)
            })

        recyclerHistory.adapter = historyAdapter

        recyclerHistory.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        showHistoryUi()

        clearBtn.setOnClickListener {
            searchField.setText("")
            currentCall?.cancel()
            progressBar.isVisible = false
            hideErrorViews()
            recycler.isVisible = false
            listOfTracks.clear()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchField.windowToken, 0)
            updateHistoryAdapter()
            showHistoryUi()
        }

        historyButton.setOnClickListener {
            sharedPreferences.edit()
                .remove(HISTORY_TRACK_KEY)
                .apply()
            hideHistoryUi()
        }
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

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackApi::class.java)

    fun searchTrack(text: String) {
        currentCall?.cancel()
        hideErrorViews()
        lastSearchQuery = text
        progressBar.isVisible = true
        currentCall = trackService.search(text)
        trackService
            .search(text)
            .enqueue(object : Callback<TrackResponse> {

                override fun onResponse(
                    call: Call<TrackResponse?>,
                    response: Response<TrackResponse?>
                ) {
                    progressBar.isVisible = false
                    if (response.isSuccessful) {
                        val trackResponse = response.body()
                        if (trackResponse != null) {
                            listOfTracks.clear()
                            listOfTracks.addAll(trackResponse.results)
                            recycler.adapter?.notifyDataSetChanged()
                            if (listOfTracks.isEmpty()) {
                                showEmptyState()
                            } else {
                                recycler.visibility = View.VISIBLE
                            }
                        } else {
                            showEmptyState()
                        }
                    } else {
                        showInternetError()
                    }

                }

                override fun onFailure(call: Call<TrackResponse?>, t: Throwable) {
                    progressBar.isVisible = false
                    if (call.isCanceled()) {
                        return
                    }
                    showInternetError()
                }
            })
    }
    fun hideErrorViews() {
        buttonError.isVisible = false
        textViewError.isVisible = false
        imageViewError.isVisible = false
    }

    fun showEmptyState() {
        textViewError.isVisible = true
        textViewError.text = getString(R.string.empty_error)
        imageViewError.isVisible = true
        if (isNightMode) {
            imageViewError.setImageResource(R.drawable.empty_error_dark_120x120)
        } else {
            imageViewError.setImageResource(R.drawable.empty_error_light_120x120)
        }
    }

    fun showInternetError() {
        textViewError.isVisible = true
        textViewError.text = getString(R.string.internet_error)
        imageViewError.isVisible = true
        buttonError.visibility = View.VISIBLE
        recycler.visibility = View.GONE
        if (isNightMode) {
            imageViewError.setImageResource(R.drawable.internet_error_dark_120x120)
        } else {
            imageViewError.setImageResource(R.drawable.internet_error_light_120x120)
        }
    }

    fun showHistoryUi() {
        if (searchHistory.getFromSharedPreference().isNotEmpty()) {
            linearLayoutHistory.visibility = View.VISIBLE
        } else {
            linearLayoutHistory.visibility = View.GONE
        }
    }

    fun hideHistoryUi() {
        linearLayoutHistory.visibility = View.GONE
    }

    private fun updateHistoryAdapter() {
        Log.d("TEST", "updateHistoryAdapter() вызван")
        val historyList = searchHistory.getFromSharedPreference()
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
            // Показываем историю только если есть что показывать
            showHistoryUi()
            // Скрываем результаты поиска
            recycler.visibility = View.GONE
        } else {
            hideHistoryUi()
            // Показываем результаты поиска, если они есть
            if (listOfTracks.isNotEmpty()) {
                recycler.visibility = View.VISIBLE
            }
        }
    }
}