package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_table")
    fun getTracks(): Flow<List<TrackEntity>>

    @Query("DELETE FROM playlist_table WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)
}