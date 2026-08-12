package com.example.playlistmaker.di

import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.impl.PlaylistInteractorImpl
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.settings.SettingInteractor
import com.example.playlistmaker.domain.settings.impl.SettingInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TrackInteractor> {
        TrackInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<SettingInteractor> {
        SettingInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    single<PlayerInteractor>{
        PlayerInteractorImpl(get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}