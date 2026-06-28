package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.domain.settings.SettingInteractor
import com.example.playlistmaker.domain.settings.SettingsRepository

class SettingInteractorImpl(
    private val preferences: SettingsRepository,
) : SettingInteractor {
    override fun isDarkTheme(): Boolean {
        return preferences.isDarkTheme()
    }

    override fun applyTheme(isDark: Boolean) {
        preferences.saveTheme(isDark)
        preferences.applyTheme(isDark)
    }
}