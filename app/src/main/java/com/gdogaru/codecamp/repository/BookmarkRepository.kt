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

package com.gdogaru.codecamp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.gdogaru.codecamp.api.model.Session
import com.gdogaru.codecamp.db.Bookmark
import com.gdogaru.holidaywish.db.BookmarkDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

@Singleton
class BookmarkRepository
@Inject constructor(val bookmarkDao: BookmarkDao) {

    fun getBookmarked(eventId: String): LiveData<Set<String>> {
        return Transformations.map(bookmarkDao.listByEventId(eventId)) { s ->
            s.map { it.sessionId }.toSet()
        }
    }

    fun addBookmarked(eventId: String, itemId: String) {
        bookmarkDao.save(Bookmark(eventId, itemId))
    }

    fun isBookmarked(eventId: String, session: Session): LiveData<Boolean> {
        return isBookmarked(eventId, session.id)
    }

    fun isBookmarked(eventId: String, sessionId: String): LiveData<Boolean> {
        return Transformations.map(
            bookmarkDao.getBookmark(eventId, sessionId)
        ) { it != null }
    }

    suspend fun setBookmarked(eventId: String, sessionId: String, checked: Boolean) {
        withContext(IO) {
            if (checked) {
                bookmarkDao.save(Bookmark(eventId, sessionId))
            } else {
                bookmarkDao.delete(eventId, sessionId)
            }
        }
    }

    suspend fun keepOnlyEvents(events: Set<String>) {
        withContext(IO) {
            bookmarkDao.deleteByEventIdExcept(events)
        }
    }
}
