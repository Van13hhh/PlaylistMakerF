package com.example.playlistmaker.data.settings.impl

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.settings.SettingsRepository

class SettingsRepositoryImpl(context: Context): SettingsRepository {
    private val prefs = context.getSharedPreferences("theme_switcher", Context.MODE_PRIVATE)

    override fun isDarkTheme(): Boolean = prefs.getBoolean("theme_switcher", false)
    @SuppressLint("UseKtx")
    override fun saveTheme(isDark: Boolean) {
        prefs.edit().putBoolean("theme_switcher", isDark).apply()
    }

    override fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}