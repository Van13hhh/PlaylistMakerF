package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val clearBtn = findViewById<Button>(R.id.clearIcon)
        val backButon = findViewById<Button>(R.id.back_to_main_menu_search)
        val edText = findViewById<EditText>(R.id.ed_text_search)


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




        val simpleTextWatcher = object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!s.isNullOrEmpty())
                    clearBtn.visibility = clearButtonVisability(s)
                else
                    clearBtn.visibility = View.GONE

                value = edText.text.toString()
            }


        }
        edText.addTextChangedListener(simpleTextWatcher)
    }
    private fun clearButtonVisability(s: CharSequence?): Int{
        return if (s.isNullOrEmpty())
            View.GONE
        else
            View.VISIBLE
    }
    private var value: String = EMPTY_TEXT

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
        const val USER_TEXT = "USER_TEXT"
        const val EMPTY_TEXT = ""
    }
}