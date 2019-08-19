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

package com.gdogaru.codecamp.view.speaker

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdogaru.codecamp.api.model.Schedule
import com.gdogaru.codecamp.api.model.Session
import com.gdogaru.codecamp.api.model.Speaker
import com.gdogaru.codecamp.api.model.Track
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.BookmarkRepository
import com.gdogaru.codecamp.repository.CodecampRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SpeakerInfoViewModel @Inject constructor(
        val repository: CodecampRepository,
        val bookmarkRepository: BookmarkRepository,
        val preferences: AppPreferences
) : ViewModel() {


    fun getSpeakerFull(speakerId: String): LiveData<FullSpeakerData?> {
        return Transformations.switchMap(repository.currentEvent) { event ->
            Transformations.map(bookmarkRepository.getBookmarked(event.refId.toString())) { favs ->
                val speaker = event.speakers.orEmpty().firstOrNull { it.name == speakerId }
                val allTracks = event.schedules.orEmpty().map { s -> s.tracks.map { t -> t to s } }.flatten().map { it.first.name to it }.toMap()
                val sessions = event.schedules.orEmpty()
                        .map { it.sessions }.flatten().filter { it.speakerIds.orEmpty().contains(speakerId) }
                        .map {
                            val p = allTracks[it.track]
                            SessionData(it,
                                    p?.first,
                                    p?.second!!,
                                    favs.contains(it.id))
                        }
                if (speaker == null) null else FullSpeakerData(speaker, sessions)
            }
        }


    }

    fun setBookmarked(element: String, checked: Boolean) {
        viewModelScope.launch {
            bookmarkRepository.setBookmarked(preferences.activeEvent.toString(), element, checked)
        }
    }
}

data class FullSpeakerData(
        val speaker: Speaker,
        val sessions: List<SessionData>
)

data class SessionData(
        val session: Session,
        val track: Track?,
        val schedule: Schedule,
        val isBookmarked: Boolean
)

