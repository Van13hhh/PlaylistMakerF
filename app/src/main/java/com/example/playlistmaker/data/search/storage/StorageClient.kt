package com.example.playlistmaker.data.search.storage

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
    fun clearData()
}