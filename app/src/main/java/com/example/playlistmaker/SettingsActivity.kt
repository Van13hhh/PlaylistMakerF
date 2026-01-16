package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val btnBackToMainMenu = findViewById<Button>(R.id.back_to_main_menu)
        val btnUserAgreement = findViewById<Button>(R.id.btn_user_agreement)
        val btnSupport = findViewById<Button>(R.id.btn_support)
        val btnShare = findViewById<Button>(R.id.btn_share)
        val frm_2 = findViewById<FrameLayout>(R.id.settings_frameL2)
        val frm_3 = findViewById<FrameLayout>(R.id.settings_frameL3)
        val frm_4 = findViewById<FrameLayout>(R.id.settings_frameL4)

        btnBackToMainMenu.setOnClickListener {
            finish()
        }
        btnUserAgreement.setOnClickListener {
            val url = Uri.parse(this.getString(R.string.url_yandex))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
        frm_4.setOnClickListener {
            val url = Uri.parse(this.getString(R.string.url_yandex))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        frm_3.setOnClickListener {
            val theme = this.getString(R.string.theme)
            val message = this.getString(R.string.message)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("novikovivan06@gmail.com"))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, theme)
            startActivity(shareIntent)
        }
        btnSupport.setOnClickListener {
            val theme = this.getString(R.string.theme)
            val message = this.getString(R.string.message)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("novikovivan06@gmail.com"))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, theme)
            startActivity(shareIntent)
        }
        frm_2.setOnClickListener {
            val message = this.getString(R.string.url_share)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooser = Intent.createChooser(shareIntent, "Send in ...")
            startActivity(chooser)
        }
        btnShare.setOnClickListener {
            val message = this.getString(R.string.url_share)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooser = Intent.createChooser(shareIntent, "Send in ...")
            startActivity(chooser)
        }
    }
}