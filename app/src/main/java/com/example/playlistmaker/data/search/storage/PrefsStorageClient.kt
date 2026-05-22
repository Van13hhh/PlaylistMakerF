package com.example.playlistmaker.data.search.storage

import android.content.Context
import com.google.gson.Gson
import java.lang.reflect.Type
import androidx.core.content.edit

class StorageClientImpl<T>(
    context: Context,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {

    private val sharedPreferences = context.getSharedPreferences(dataKey, Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun storeData(data: T) {
        val json = gson.toJson(data, type)
        sharedPreferences.edit { putString(dataKey, json) }
    }

    override fun getData(): T? {
        val json = sharedPreferences.getString(dataKey, null) ?: return null
        return gson.fromJson(json, type)
    }

    override fun clearData() {
        sharedPreferences.edit { remove(dataKey) }
    }

}