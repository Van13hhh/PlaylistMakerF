package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.bumptech.glide.Glide

class SearchActivity : AppCompatActivity() {
    private var value: String = EMPTY_TEXT
    private val listOfTracks: List<Track> =listOf(
        Track(
            "Smells Like Teen Spirit",
            "Nirvana",
            "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
        Track(
            "Billie Jean",
            "Michael Jackson",
            "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg",
        ),
        Track(
            "Stayin' Alive",
            "Bee Gees",
            "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg",
        ),
        Track(
            "Whole Lotta Love",
            "Led Zeppelin",
            "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg",
        ),
        Track(
            "Sweet Child O'Mine",
            "Guns N' Roses",
            "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg",
        )
    )
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val clearBtn = findViewById<Button>(R.id.clearIcon)
        val backButon = findViewById<Button>(R.id.back_to_main_menu_search)
        val edText = findViewById<EditText>(R.id.ed_text_search)
        val recycler = findViewById<RecyclerView>(R.id.rv_search)

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
        val edText = findViewById<EditText>(R.id.ed_text_search)
        val clearBtn = findViewById<Button>(R.id.clearIcon)

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
}

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val trackImage: ImageView
    private val trackName: TextView
    private val trackAuthorName: TextView

    init {
        trackImage = itemView.findViewById(R.id.iv_trackImage)
        trackName = itemView.findViewById(R.id.tv_trackName)
        trackAuthorName = itemView.findViewById(R.id.tv_authorName)
    }
    fun bind(model: Track){
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.outline_android_wifi_3_bar_off_24)
            .centerCrop()
            .into(trackImage)
        trackImage.setBackgroundResource(R.drawable.rounded_corners);
        trackImage.setClipToOutline(true)

        trackName.text = model.trackName
        trackAuthorName.text = "${model.artistName} · ${model.trackTime}"
    }
}
class TrackAdapter(
    private val listOfTracks: List<Track>
): RecyclerView.Adapter<TrackViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.activity_rv_search,
            parent,
            false
        )
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(listOfTracks[position])
    }

    override fun getItemCount() = listOfTracks.size

}

