package com.gdogaru.codecamp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.gdogaru.codecamp.api.model.*
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.CodecampRepository
import com.gdogaru.codecamp.svc.BookmarkingService
import javax.inject.Inject

class MainViewModel
@Inject constructor(val repository: CodecampRepository,
                    val preferences: AppPreferences,
                    val bookmarkingService: BookmarkingService) : ViewModel() {

    val currentEvent: LiveData<Codecamp> = Transformations.switchMap(preferences.activeEventLiveData) { repository.eventData(it) }

    fun allEvents() = repository.events()

    fun setActiveEvent(refId: Long) {
        preferences.activeEvent = refId
    }

    fun getSession(sessionId: String): LiveData<FullSessionData?> {
        return Transformations.map(currentEvent) { event ->
            val session = event.schedules.orEmpty().map { it.sessions }.flatten().firstOrNull { it.id == sessionId }
                    ?: return@map null
            val track = event.schedules.orEmpty().map { it.tracks }.flatten().firstOrNull { it.name == session.track }
            val speakers = event.speakers.orEmpty().filter { session.speakerIds.orEmpty().contains(it.name) }
            FullSessionData(session, track, speakers)
        }
    }

    fun getSpeaker(speakerId: String): LiveData<Speaker> {
        return Transformations.map(currentEvent) { event ->
            event.speakers.orEmpty().first { it.name == speakerId }
        }
    }

    fun currentSchedule(): LiveData<Schedule?> {
        return Transformations.switchMap(preferences.activeScheduleLiveData) { s ->
            Transformations.map(currentEvent) { if (it.schedules?.size ?: 0 <= s) null else it.schedules!![s] }
        }
    }

    fun getSpeakerFull(speakerId: String): LiveData<FullSpeakerData?> {
        return Transformations.map(currentEvent) { event ->
            val speaker = event.speakers.orEmpty().firstOrNull { it.name == speakerId }!!
            val allTracks = event.schedules.orEmpty().map { s -> s.tracks.map { t -> t to s } }.flatten().map { it.first.name to it }.toMap()
            val sessions = event.schedules.orEmpty()
                    .map { it.sessions }.flatten().filter { it.speakerIds.orEmpty().contains(speakerId) }
                    .map {
                        val p = allTracks[it.track]
                        it to TrackData(p?.first, p?.second!!)
                    }
            FullSpeakerData(speaker, sessions)
        }
    }

    fun setBookmarked(element: String, checked: Boolean) {
        return bookmarkingService.setBookmarked(preferences.activeEvent.toString(), element, checked)
    }

    fun isBookmarked(s: String): Boolean {
        return bookmarkingService.isBookmarked(preferences.activeEvent.toString(), s)
    }

    fun getBookmarked(): Set<String> {
        return bookmarkingService.getBookmarked(preferences.activeEvent.toString())
    }

    fun loadingProgress() = preferences.updateProgressLiveData

    fun selectSchedule(idx: Int) {
        preferences.activeSchedule = idx
    }
}


data class FullSessionData(
        val session: Session,
        val track: Track?,
        val speakers: List<Speaker>
)

data class FullSpeakerData(
        val speaker: Speaker,
        val sessions: List<Pair<Session, TrackData>>
)

data class TrackData(
        val track: Track?,
        val schedule: Schedule
)