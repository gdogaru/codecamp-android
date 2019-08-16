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

package com.gdogaru.codecamp.view.agenda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gdogaru.codecamp.api.model.Schedule
import com.gdogaru.codecamp.api.model.Session
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.BookmarkRepository
import com.gdogaru.codecamp.repository.CodecampRepository

fun scheduleFavMediator(repository: CodecampRepository, bookmarkRepository: BookmarkRepository, preferences: AppPreferences, favoritesOnly: MutableLiveData<Boolean>): LiveData<Pair<Schedule?, Set<String>>> {

    val mediator = MediatorLiveData<Pair<Schedule?, Set<String>>>()

    mediator.value = Pair(null, setOf())
    mediator.addSource(repository.currentSchedule()) {
        val prev = mediator.value
        prev?.let { p ->
            mediator.value = Pair(it, p.second)
        }
    }
    mediator.addSource(bookmarkRepository.getBookmarked(preferences.activeEvent.toString())) {
        val prev = mediator.value
        prev?.let { p ->
            mediator.value = Pair(p.first, it)
        }
    }

    mediator.addSource(favoritesOnly) {
        mediator.value = mediator.value
    }
    return mediator
}

val SESSION_BY_DATE_COMPARATOR = Comparator<Session> { lhs: Session, rhs: Session -> lhs.startTime!!.compareTo(rhs.startTime!!) }
