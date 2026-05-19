package com.example.playlistmaker.domain.settings

interface SettingInteractor {
    fun isDarkTheme(): Boolean
    fun applyTheme(isDark: Boolean)
}