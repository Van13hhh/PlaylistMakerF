package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


class App: Application() {

    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(SETTING_SWITCHER_THEME, MODE_PRIVATE)

        darkTheme = sharedPreferences.getBoolean(SETTING_SWITCHER_THEME, false)

        switchTheme(darkTheme)
    }


    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
