package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val btnBackToMainMenu = findViewById<Button>(R.id.back_to_main_menu)
        val btnUserAgreement = findViewById<Button>(R.id.btn_user_agreement)
        val btnSupport = findViewById<Button>(R.id.btn_support)
        val btnShare = findViewById<Button>(R.id.btn_share)

        btnBackToMainMenu.setOnClickListener {
            finish()
        }
        btnUserAgreement.setOnClickListener {
            val url = Uri.parse("https://yandex.ru/legal/practicum_offer/ru")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        btnSupport.setOnClickListener {
            val theme = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("novikovivan06@gmail.com"))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, theme)
            startActivity(shareIntent)
        }
        btnShare.setOnClickListener {
            val message = "https://practicum.yandex.ru/android-developer"
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooser = Intent.createChooser(shareIntent, "Send in ...")
            startActivity(chooser)
        }
    }
}