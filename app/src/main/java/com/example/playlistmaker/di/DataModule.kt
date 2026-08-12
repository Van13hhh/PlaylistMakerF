package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.search.network.ItunesApiService
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClientImpl
import com.example.playlistmaker.data.search.storage.StorageClient
import com.example.playlistmaker.data.search.storage.StorageClientImpl
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.converters.PlaylistDbConvertor
import com.example.playlistmaker.util.converters.PlaylistTrackConvertor
import com.example.playlistmaker.util.converters.TrackConverter
import com.example.playlistmaker.util.converters.TrackDbConvertor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
val dataModule = module {

    single { Gson() }

    single<Executor> { Executors.newFixedThreadPool(2) }

    single<RetrofitNetworkClient> {
        RetrofitNetworkClientImpl(androidContext(), get())
    }

    single<StorageClient<List<Track>>>(named("search_history")) {
        StorageClientImpl(
            dataKey = "search_history",
            type = object : TypeToken<List<Track>>() {}.type,
            gson = get(),
            sharedPreferences = get(named("search_history"))
        )
    }

    single<TrackConverter> {
        TrackConverter()
    }

    single<TrackDbConvertor> {
        TrackDbConvertor()
    }

    single<PlaylistDbConvertor> {
        PlaylistDbConvertor(get())
    }

    single<PlaylistTrackConvertor> {
        PlaylistTrackConvertor()
    }

    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    single<AppDatabase>{
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

}
val preferenceModule = module {
    single<SharedPreferences>(named("theme_switcher")) {
        androidContext().getSharedPreferences("theme_switcher", Context.MODE_PRIVATE)
    }
    single<SharedPreferences>(named("search_history")) {
        androidContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }
}