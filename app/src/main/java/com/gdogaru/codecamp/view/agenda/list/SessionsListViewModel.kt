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

package com.gdogaru.codecamp.view.agenda.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.gdogaru.codecamp.api.model.Schedule
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.BookmarkRepository
import com.gdogaru.codecamp.repository.CodecampRepository
import com.gdogaru.codecamp.util.DateUtil
import com.gdogaru.codecamp.view.agenda.SESSION_BY_DATE_COMPARATOR
import com.gdogaru.codecamp.view.agenda.scheduleFavMediator
import org.threeten.bp.LocalTime
import java.util.*
import javax.inject.Inject

class SessionsListViewModel @Inject constructor(
    repository: CodecampRepository,
    bookmarkRepository: BookmarkRepository,
    preferences: AppPreferences
) : ViewModel() {

    private val favoritesOnly = MutableLiveData(false)
    private val currentScheduleFav =
        scheduleFavMediator(repository, bookmarkRepository, preferences, favoritesOnly)

    fun sessionItems(): LiveData<List<AgendaListItem>> {
        return Transformations.map(currentScheduleFav) { s ->
            s?.let {
                it.first?.let { s ->
                    listItems(s, it.second)
                }
            }
        }
    }

    private fun listItems(
        schedule: Schedule,
        favorites: Set<String>
    ): List<AgendaListItem> {
        val sessions = schedule.sessions.orEmpty().sortedWith(SESSION_BY_DATE_COMPARATOR)
        val sessionListItems = mutableListOf<AgendaListItem.SessionListItem>()
        val fav = favoritesOnly.value ?: false

        for (s in sessions) {
            if (!fav || favorites.contains(s.id)) {
                val item = AgendaListItem.SessionListItem()
                item.name = s.title
                item.id = s.id
                item.end = s.endTime
                item.start = s.startTime
                item.trackName = s.track
                item.speakerNames = ArrayList(s.speakerIds.orEmpty())
                item.bookmarked = favorites.contains(s.id)
                sessionListItems.add(item)
            }
        }

        val result = mutableListOf<AgendaListItem>()
        var prevSlot: Pair<LocalTime?, LocalTime?> = (null to null)
        sessionListItems.forEach { session ->
            val slot = session.start to session.end
            if (slot != prevSlot) {
                result.add(
                    AgendaListItem.HeaderListItem(
                        DateUtil.formatPeriod(
                            slot.first,
                            slot.second
                        )
                    )
                )
                prevSlot = slot
            }
            result.add(session)
        }
        return result
    }

    fun setFavoritesOnly(favoritesOnly: Boolean) {
        this.favoritesOnly.value = favoritesOnly
    }

}
