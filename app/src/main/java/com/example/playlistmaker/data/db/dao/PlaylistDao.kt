package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT listOfTrackIds FROM playlist_table WHERE id = :playlistId")
    suspend fun getListWithTrackIds(playlistId: Long): String?

    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    suspend fun getPlaylist(playlistId: Long): PlaylistEntity

    @Query("SELECT listOfTrackIds FROM playlist_table")
    suspend fun getAllListOfTracks(): List<String>

    @Delete
    suspend fun deleteTrack(playlist: PlaylistEntity)

}