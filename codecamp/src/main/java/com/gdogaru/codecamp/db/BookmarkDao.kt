package com.gdogaru.holidaywish.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gdogaru.codecamp.db.Bookmark

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(favoritesMessage: Bookmark)

    @Query("select * from bookmarks as b where b.eventId=:eventId and b.sessionId=:sessionId")
    fun getBookmark(eventId: String, sessionId: String): LiveData<Bookmark?>

    @Query("select * from bookmarks as b where b.eventId=:eventId")
    fun listByEventId(eventId: String): LiveData<List<Bookmark>>

    @Query("delete from bookmarks where eventId=:eventId")
    fun deleteByEventId(eventId: String)

    @Query("delete from bookmarks where eventId=:eventId and sessionId=:sessionId")
    fun delete(eventId: String, sessionId: String)

    @Query("delete from bookmarks where eventId not in (:eventIds)")
    fun deleteByEventIdExcept(eventIds: Set<String>)
}
