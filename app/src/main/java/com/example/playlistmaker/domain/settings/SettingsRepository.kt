package com.example.playlistmaker.domain.settings

interface SettingsRepository {
    fun isDarkTheme(): Boolean
    fun saveTheme(isDark: Boolean)
    fun applyTheme(isDark: Boolean)
}