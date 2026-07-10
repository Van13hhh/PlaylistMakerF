package com.example.playlistmaker.di

import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.TrackRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> {
        TrackRepositoryImpl(
            get()
        )
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(named("search_history")))
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named("theme_switcher")))
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}