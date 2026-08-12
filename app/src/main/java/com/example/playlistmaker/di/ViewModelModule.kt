package com.example.playlistmaker.di

import com.example.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.media.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.playlist.view_model.PlaylistCreateViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        AudioPlayerViewModel(get(), get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }
    viewModel {
        FavoriteTracksViewModel(get())
    }
    viewModel {
        PlaylistCreateViewModel(get())
    }
    viewModel {
        PlaylistsViewModel(get())
    }
}