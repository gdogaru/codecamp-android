/*
 * Copyright (c) 2019 Gabriel Dogaru - gdogaru@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
