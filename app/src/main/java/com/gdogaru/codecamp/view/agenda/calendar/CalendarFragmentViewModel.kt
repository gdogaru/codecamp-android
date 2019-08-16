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

package com.gdogaru.codecamp.view.agenda.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.gdogaru.codecamp.api.model.Schedule
import com.gdogaru.codecamp.api.model.Session
import com.gdogaru.codecamp.api.model.Track
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.BookmarkRepository
import com.gdogaru.codecamp.repository.CodecampRepository
import com.gdogaru.codecamp.util.Joiner
import com.gdogaru.codecamp.view.agenda.SESSION_BY_DATE_COMPARATOR
import com.gdogaru.codecamp.view.agenda.calendar.component.CEvent
import com.gdogaru.codecamp.view.agenda.scheduleFavMediator
import org.threeten.bp.LocalTime
import java.util.*
import javax.inject.Inject

class CalendarFragmentViewModel @Inject constructor(
        repository: CodecampRepository,
        bookmarkRepository: BookmarkRepository,
        preferences: AppPreferences)
    : ViewModel() {

    val favoritesOnly = MutableLiveData<Boolean>(false)
    val currentScheduleFav = scheduleFavMediator(repository, bookmarkRepository, preferences, favoritesOnly)

    fun eventList(): LiveData<Pair<List<CEvent>, Schedule>> {
        return Transformations.map(currentScheduleFav) {
            Pair(toCEvents(it.first!!, it.second), it.first!!)
        }
    }

    fun toCEvents(schedule: Schedule, bookmarked: Set<String>): List<CEvent> {
        val sessions = schedule.sessions.toMutableList()
        val tracks = schedule.tracks.toMutableList()

        if (favoritesOnly.value == true) {
            sessions.removeAll { !bookmarked.contains(it.id) }
            val rt = sessions.filter { it.track != null }.map { it.track }.toSet()
            tracks.removeAll { !rt.contains(it.name) }
        }

        val events = mutableListOf<CEvent>()
        Collections.sort(sessions, SESSION_BY_DATE_COMPARATOR)

        sessions.map { ss ->
            var preferedIdx = 0
            if (ss.track != null) {
                val track = getTrack(tracks, ss.track!!)
                if (track != null) preferedIdx = track.displayOrder
            }
            val descLine2 = ss.track
            events.add(CEvent(ss.id, ss.startTime
                    ?: LocalTime.MIN, ss.endTime
                    ?: LocalTime.MIN, preferedIdx, ss.title,
                    createSpeakerName(ss),
                    if (descLine2 == null) "" else descLine2,
                    bookmarked.contains(ss.id)
            ))
        }
        return events
    }

    private fun getTrack(tracks: List<Track>, track: String): Track? {
        for (t in tracks) {
            if (t.name == track) {
                return t
            }
        }
        return null
    }

    private fun createSpeakerName(session: Session): String {
        return if (session.speakerIds == null || session.speakerIds!!.isEmpty()) "" else Joiner.on(", ").join(session.speakerIds!!)
    }

    fun setFavoritesOnly(favoritesOnly: Boolean) {
        this.favoritesOnly.value = favoritesOnly
    }

}