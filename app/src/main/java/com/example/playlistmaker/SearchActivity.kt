package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
class SearchActivity : AppCompatActivity() {
    private var value: String = EMPTY_TEXT
    private lateinit var clearBtn: Button
    private lateinit var backButon: Button
    private lateinit var edText: EditText
    private lateinit var recycler: RecyclerView
    private lateinit var textViewError: TextView
    private lateinit var buttonError: Button
    private lateinit var imageViewError: ImageView
    private var isNightMode = false

    private var lastSearchQuery = ""

    private var listOfTracks: MutableList<Track> = mutableListOf()
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
        edText = findViewById(R.id.ed_text_search)
        recycler = findViewById(R.id.rv_search)
        textViewError = findViewById(R.id.tv_error)
        buttonError = findViewById(R.id.btn_error)
        imageViewError = findViewById(R.id.iv_error)
        listOfTracks = mutableListOf()


        recycler.adapter = TrackAdapter(listOfTracks)

        recycler.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        edText.setText(value)
        clearBtn.visibility = clearButtonVisability(value)
        edText.requestFocus()
        edText.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edText, InputMethodManager.SHOW_IMPLICIT)
        }, 200)

        backButon.setOnClickListener {
            finish()
        }

        clearBtn.setOnClickListener {
            edText.setText("")
            hideErrorViews()
            recycler.visibility = View.GONE
            listOfTracks.clear()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(edText.windowToken, 0)
        }

        edText.doOnTextChanged { s, start, before, count ->
            if (!s.isNullOrEmpty())
                clearBtn.visibility = clearButtonVisability(s)
            else
                clearBtn.visibility = View.GONE

            value = edText.text.toString()
        }

        edText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = edText.text.toString()
                searchTrack(text)
            }
            false
        }
        buttonError.setOnClickListener {
            hideErrorViews()
            searchTrack(lastSearchQuery)
        }
    }
    private fun clearButtonVisability(s: CharSequence?): Int{
        return if (s.isNullOrEmpty())
            View.GONE
        else
            View.VISIBLE
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        value = savedInstanceState.getString(USER_TEXT, EMPTY_TEXT)


        edText.setText(value)
        clearBtn.visibility = clearButtonVisability(value)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USER_TEXT, value)
    }
    companion object {
        private const val USER_TEXT = "USER_TEXT"
        private const val EMPTY_TEXT = ""
    }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    interface TrackApi{
        @GET("/search?entity=song")
        fun search(@Query("term") text: String): Call<TrackResponse>
    }

    private val trackService = retrofit.create(TrackApi::class.java)

    fun searchTrack(text: String){
        hideErrorViews()
        lastSearchQuery = text
        trackService
            .search(text)
            .enqueue( object : Callback<TrackResponse>{

                override fun onResponse(call: Call<TrackResponse?>, response: Response<TrackResponse?>){
                    if (response.isSuccessful){
                        val trackResponse = response.body()
                        if (trackResponse != null){
                            listOfTracks.clear()
                             listOfTracks.addAll(trackResponse.results)
                            recycler.adapter?.notifyDataSetChanged()
                            if (listOfTracks.isEmpty()){
                                showEmptyState()
                            }else {
                                recycler.visibility = View.VISIBLE
                            }
                        }else{
                            showEmptyState()
                        }
                    }else {
                        showInternetError()
                    }

                }

                override fun onFailure(call: Call<TrackResponse?>, t: Throwable){
                    showInternetError()
                }
            })
    }
    fun hideErrorViews(){
        buttonError.visibility = View.GONE
        textViewError.visibility = View.GONE
        imageViewError.visibility = View.GONE
    }
    fun showEmptyState(){
        textViewError.visibility = View.VISIBLE
        textViewError.text = getString(R.string.empty_error)
        imageViewError.visibility = View.VISIBLE
        if (isNightMode) {
            imageViewError.setImageResource(R.drawable.empty_error_dark_120x120)
        } else{
            imageViewError.setImageResource(R.drawable.empty_error_light_120x120)
        }

    }
    fun showInternetError(){
        textViewError.visibility = View.VISIBLE
        textViewError.text = getString(R.string.internet_error)
        imageViewError.visibility = View.VISIBLE
        buttonError.visibility = View.VISIBLE
        recycler.visibility = View.GONE
        if (isNightMode) {
            imageViewError.setImageResource(R.drawable.internet_error_dark_120x120)
        } else{
            imageViewError.setImageResource(R.drawable.internet_error_light_120x120)
        }
    }
}