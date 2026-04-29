package com.example.playlistmaker.domain.interactors

interface SettingInteractor {
    fun isDarkTheme(): Boolean
    fun applyTheme(isDark: Boolean)
}