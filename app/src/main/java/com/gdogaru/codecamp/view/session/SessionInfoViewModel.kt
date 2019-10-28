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

package com.gdogaru.codecamp.view.session

import androidx.lifecycle.*
import com.gdogaru.codecamp.api.model.Session
import com.gdogaru.codecamp.api.model.Speaker
import com.gdogaru.codecamp.api.model.Track
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.BookmarkRepository
import com.gdogaru.codecamp.repository.CodecampRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SessionInfoViewModel @Inject constructor(
    private val repository: CodecampRepository,
    private val bookmarkRepository: BookmarkRepository,
    val preferences: AppPreferences
) : ViewModel() {

    fun getSession(sessionId: String): LiveData<FullSessionData?> =
        Transformations.map(repository.currentEvent) { event ->
            event.schedules.orEmpty()
                .map { it.sessions }.flatten()
                .firstOrNull { it.id == sessionId }
                ?.let { session ->
                    val track = event.schedules.orEmpty().map { it.tracks }.flatten()
                        .firstOrNull { it.name == session.track }
                    val speakers = event.speakers.orEmpty()
                        .filter { session.speakerIds.orEmpty().contains(it.name) }
                    FullSessionData(session, track, speakers)
                }
        }

    fun setBookmarked(element: String, checked: Boolean) {
        viewModelScope.launch {
            bookmarkRepository.setBookmarked(preferences.activeEvent.toString(), element, checked)
        }
    }

    fun isBookmarked(s: String): LiveData<Boolean> {
        val ev = repository.currentEvent.value
        return if (ev != null) bookmarkRepository.isBookmarked(
            ev.refId.toString(),
            s
        ) else MutableLiveData(false)
    }
}

data class FullSessionData(
    val session: Session,
    val track: Track?,
    val speakers: List<Speaker>
)
