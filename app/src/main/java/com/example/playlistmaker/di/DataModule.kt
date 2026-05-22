package com.example.playlistmaker.di

import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClientImpl
import com.example.playlistmaker.data.search.storage.StorageClient
import com.example.playlistmaker.data.search.storage.StorageClientImpl
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.TrackConverter
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<RetrofitNetworkClient> {
        RetrofitNetworkClientImpl(androidContext())
    }

    single<StorageClient<List<Track>>> {
        StorageClientImpl<List<Track>>(
            context = androidContext(),
            dataKey = "search_history",
            type = object : TypeToken<List<Track>>() {}.type
        )
    }

    single<TrackConverter> {
        TrackConverter()
    }
}