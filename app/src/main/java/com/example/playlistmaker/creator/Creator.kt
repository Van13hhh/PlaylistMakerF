package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.settings.impl.SettingInteractorImpl
import com.example.playlistmaker.data.search.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.storage.PrefsStorageClient
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.settings.SettingInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.TrackRepository
import com.example.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.TrackConverter
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.google.gson.reflect.TypeToken

object Creator {
    private fun getTrackRepository(context: Context): TrackRepository{
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor{
        return TrackInteractorImpl(getTrackRepository(context))
    }

    fun provideTrackConverter(): TrackConverter {
        return TrackConverter()
    }

    fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun getSettingsInteractor(context: Context): SettingInteractor{
        return SettingInteractorImpl(
            getSettingsRepository(context))
    }

    fun getSharingInteractor(context: Application): SharingInteractor{
        return SharingInteractorImpl(ExternalNavigatorImpl(context))
    }

    fun getSearchHistoryInteractor(context: Context): SearchHistoryInteractor{
        return SearchHistoryInteractorImpl(SearchHistoryRepositoryImpl(PrefsStorageClient(
            context,
            "HISTORY",
            object : TypeToken<ArrayList<Track>>() {}.type)))
    }
}