package com.example.playlistmaker.presentation.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator

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

        val sharingInteractor = Creator.getSharingInteractor(this)
        val themeManager = Creator.getSettingsInteractor(this)

        val btnBackToMainMenu = findViewById<Button>(R.id.back_to_main_menu)
        val btnUserAgreement = findViewById<Button>(R.id.btn_user_agreement)
        val btnSupport = findViewById<Button>(R.id.btn_support)
        val btnShare = findViewById<Button>(R.id.btn_share)
        val sharingFrame = findViewById<FrameLayout>(R.id.settings_frameL2)
        val supportFrame = findViewById<FrameLayout>(R.id.settings_frameL3)
        val agreementFrame = findViewById<FrameLayout>(R.id.settings_frameL4)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        val isDarkTheme = themeManager.isDarkTheme()
        themeSwitcher.isChecked = isDarkTheme

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            themeManager.applyTheme(checked)
        }

        btnBackToMainMenu.setOnClickListener {
            finish()
        }
        btnUserAgreement.setOnClickListener {
            sharingInteractor.openAgreement()
        }
        agreementFrame.setOnClickListener {
            sharingInteractor.openAgreement()
        }

        supportFrame.setOnClickListener {
            sharingInteractor.openSupport()
        }
        btnSupport.setOnClickListener {
            sharingInteractor.openSupport()
        }
        sharingFrame.setOnClickListener {
            sharingInteractor.shareApp()
        }
        btnShare.setOnClickListener {
            sharingInteractor.shareApp()
        }

    }
}