package com.capstone.agrovision.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookmarkDao {

    @Insert
    suspend fun insertBookmark(bookmark: Bookmark)

    @Query("SELECT * FROM bookmarks")
    suspend fun getAllBookmarks(): List<Bookmark>

    @Query("SELECT * FROM bookmarks WHERE imageUri = :imageUri AND description = :description LIMIT 1")
    suspend fun getBookmarkByUriAndDescription(imageUri: String, description: String): Bookmark?

    @Query("DELETE FROM bookmarks WHERE id = :bookmarkId")
    suspend fun deleteBookmark(bookmarkId: Long)
}
