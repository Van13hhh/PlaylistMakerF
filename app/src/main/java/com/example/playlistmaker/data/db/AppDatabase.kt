package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.data.db.entity.TrackEntity

@Database(
    version = 6, entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun PlaylistTracksDao(): PlaylistTracksDao
}