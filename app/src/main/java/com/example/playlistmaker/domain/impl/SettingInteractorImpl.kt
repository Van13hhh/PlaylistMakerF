package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.interactors.SettingInteractor
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingInteractorImpl(
    private val preferences: SettingsRepository,
): SettingInteractor {
    override fun isDarkTheme(): Boolean {
        return preferences.isDarkTheme()
    }

    override fun applyTheme(isDark: Boolean) {
        preferences.saveTheme(isDark)
        preferences.applyTheme(isDark)
    }
}