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
 * Created by Gabriel on 2/15/2017.
 */

@Singleton
class BookmarkRepository
@Inject constructor(val bookmarkDao: BookmarkDao) {

    fun getBookmarked(eventId: String): LiveData<Set<String>> {
        return Transformations.map(bookmarkDao.listByEventId(eventId)) { s -> s.map { it.sessionId }.toSet() }
    }

    fun addBookmarked(eventId: String, itemId: String) {
        bookmarkDao.save(Bookmark(eventId, itemId))
    }

    fun isBookmarked(eventId: String, session: Session): LiveData<Boolean> {
        return isBookmarked(eventId, session.id)
    }

    fun isBookmarked(eventId: String, sessionId: String): LiveData<Boolean> {
        return Transformations.map(bookmarkDao.getBookmark(eventId, sessionId)
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
