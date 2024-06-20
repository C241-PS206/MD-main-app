package com.capstone.agrovision.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(bookmark: BookmarkResult)

    @Query("SELECT * FROM bookmark")
    suspend fun getAll(): List<BookmarkResult>

    @Delete
    suspend fun delete(bookmark: BookmarkResult)

    @Query("SELECT * FROM bookmark WHERE imagePath = :imagePath AND result = :result LIMIT 1")
    suspend fun getBookmarkByUriAndResult(imagePath: String, result: String): BookmarkResult?
}
