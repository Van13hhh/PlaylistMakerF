package com.example.playlistmaker.domain.repository

interface SettingsRepository {
    fun isDarkTheme(): Boolean
    fun saveTheme(isDark: Boolean)
    fun applyTheme(isDark: Boolean)
}