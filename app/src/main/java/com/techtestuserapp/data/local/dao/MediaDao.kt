package com.techtestuserapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techtestuserapp.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(mediaItem: MediaEntity)

    @Query("SELECT * FROM media_items WHERE userId = :userId")
    fun getMediaForUser(userId: Int): Flow<List<MediaEntity>>
}