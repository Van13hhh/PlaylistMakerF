package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.core.content.edit

const val SETTING_SWITCHER_THEME = "theme_switcher"

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ll_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBackToMainMenu = findViewById<Button>(R.id.back_to_main_menu)
        val btnUserAgreement = findViewById<Button>(R.id.btn_user_agreement)
        val btnSupport = findViewById<Button>(R.id.btn_support)
        val btnShare = findViewById<Button>(R.id.btn_share)
        val frm_2 = findViewById<FrameLayout>(R.id.settings_frameL2)
        val frm_3 = findViewById<FrameLayout>(R.id.settings_frameL3)
        val frm_4 = findViewById<FrameLayout>(R.id.settings_frameL4)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        val sharedPrefs = getSharedPreferences(SETTING_SWITCHER_THEME, MODE_PRIVATE)
        val isDarkTheme = sharedPrefs.getBoolean(SETTING_SWITCHER_THEME, false)

        themeSwitcher.isChecked = isDarkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            switchTheme(checked)
            sharedPrefs.edit()
                .putBoolean(SETTING_SWITCHER_THEME, checked)
                .apply()
        }

        btnBackToMainMenu.setOnClickListener {
            finish()
        }
        btnUserAgreement.setOnClickListener {
            showUserAgreement()
        }
        frm_4.setOnClickListener {
            showUserAgreement()
        }

        frm_3.setOnClickListener {
            textSupport()
        }
        btnSupport.setOnClickListener {
            textSupport()
        }
        frm_2.setOnClickListener {
            shareApp()
        }
        btnShare.setOnClickListener {
            shareApp()
        }
        switchTheme(isDarkTheme)
    }

    private fun switchTheme(checked: Boolean) {
        (application as App).switchTheme(checked)
    }

    private fun showUserAgreement() {
        val url = Uri.parse(getString(R.string.url_yandex))
        val intent = Intent(Intent.ACTION_VIEW, url)
        startActivity(intent)
    }

    private fun textSupport() {
        val theme = this.getString(R.string.theme)
        val message = this.getString(R.string.message)
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.data = Uri.parse("mailto:")
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail)))
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, theme)
        startActivity(shareIntent)
    }

    private fun shareApp() {
        val message = this.getString(R.string.url_share)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        val chooser = Intent.createChooser(shareIntent, "Send in ...")
        startActivity(chooser)
    }
}