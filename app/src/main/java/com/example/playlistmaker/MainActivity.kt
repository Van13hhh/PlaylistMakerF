package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnMedia = findViewById<Button>(R.id.btn_media)
        val btnSettings = findViewById<Button>(R.id.btn_settings)


        btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        btnMedia.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}