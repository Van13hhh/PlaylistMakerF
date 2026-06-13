package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.playerModule
import com.example.playlistmaker.di.preferenceModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.settings.SettingInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, interactorModule, repositoryModule, viewModelModule, playerModule,
                preferenceModule
            )
        }

        val settingsInteractor: SettingInteractor by inject()

        val isDarkTheme = settingsInteractor.isDarkTheme()
        settingsInteractor.applyTheme(isDarkTheme)
    }
}
