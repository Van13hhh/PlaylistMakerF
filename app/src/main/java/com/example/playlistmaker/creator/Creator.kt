package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.data.navigation.ExternalNavigatorImpl
import com.example.playlistmaker.domain.impl.SettingInteractorImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.domain.interactors.SettingInteractor
import com.example.playlistmaker.domain.interactors.SharingInteractor
import com.example.playlistmaker.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.domain.interactors.TrackInteractor
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.interactors.SearchHistoryInteractor
import com.example.playlistmaker.domain.mapers.TrackConverter
import com.example.playlistmaker.domain.repository.SettingsRepository

object Creator {
    private fun getTrackRepository(): TrackRepository{
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor{
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(AudioPlayerRepositoryImpl())
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

    fun getSharingInteractor(context: Context): SharingInteractor{
        return SharingInteractorImpl(ExternalNavigatorImpl(context))
    }

    fun getSearchHistoryInteractor(context: Context): SearchHistoryInteractor{
        return SearchHistoryInteractorImpl(SearchHistoryRepositoryImpl(context))
    }
}