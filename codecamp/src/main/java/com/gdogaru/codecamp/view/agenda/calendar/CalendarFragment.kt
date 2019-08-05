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


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.evernote.android.state.State
import com.gdogaru.codecamp.api.model.Schedule
import com.gdogaru.codecamp.api.model.Session
import com.gdogaru.codecamp.api.model.Track
import com.gdogaru.codecamp.di.Injectable
import com.gdogaru.codecamp.util.Joiner
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.agenda.AgendaFragmentDirections
import com.gdogaru.codecamp.view.agenda.SessionsFragment
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.util.*
import javax.inject.Inject


class CalendarFragment : SessionsFragment(), Injectable {

    private var sessIds = ArrayList<Int>()
    private var currentTimer: Timer? = null
    private val offset: Int = 0
    private lateinit var calendar: Calendar
    @State
    lateinit var calendarState: Calendar.CalendarState
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        calendar = Calendar(requireActivity())
        return calendar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateDisplay()
        viewModel.currentSchedule().observe(this, androidx.lifecycle.Observer { s ->
            s?.let { updateDisplay(it) }
        })
    }

    override fun onResume() {
        super.onResume()
        currentTimer = Timer()
        currentTimer!!.schedule(object : TimerTask() {
            override fun run() {
                calendar!!.post { calendar!!.updateCurrentTime(LocalDateTime.now()) }
            }
        }, 500, 30000)

        //        calendar.setBookmarked(bookmarkingService.getBookmarked(codecampClient.getEvent().getTitle()));
        //        if (calendarState != null) {
        //            calendar.postDelayed(() -> calendar.setState(calendarState), 300);
        //        }
    }

    override fun onPause() {
        super.onPause()
        if (currentTimer != null) {
            currentTimer!!.cancel()
        }
        calendarState = calendar.state
    }

    override fun updateDisplay() {
    }

    fun updateDisplay(schedule: Schedule) {
        var sessions = schedule.sessions
        var tracks = schedule.tracks
        var bookmarked = setOf<String>() //viewModel.getBookmarked()

        if (getFavoritesOnly()) {
            keepFavoritesOnly(sessions, tracks, "schedule", bookmarked);
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
            events.add(CEvent(ss.id, ss.startTime ?: LocalTime.MIN, ss.endTime
                    ?: LocalTime.MIN, preferedIdx, ss.title,
                    createSpeakerName(ss),
                    if (descLine2 == null) "" else descLine2))
        }
        initSessionIds(sessions)
        calendar.setCurrentTime(LocalDateTime.now())
        calendar.setEvents(events)
        calendar.setScheduleDate(schedule.date)

        calendar.setEventListener(object : EventListener {
            override fun eventCLicked(event: DisplayEvent) {
                displayEventDetails(event.event.id)
            }
        })
    }

    private fun keepFavoritesOnly(sessions: List<Session>, tracks: List<Track>, eventId: String, bookmarked: Set<String>) {
//        run {
//            val iterator = sessions.iterator()
//            while (iterator.hasNext()) {
//                val s = iterator.next()
//                if (!bookmarked.contains(s.id)) {
//                    iterator.remove()
//                }
//            }
//        }
//        val rt = HashSet<String>()
//        for ((_, _, _, _, _, _, _, _, track) in sessions) rt.add(track)
//        val iterator = tracks.iterator()
//        while (iterator.hasNext()) {
//            val (name) = iterator.next()
//            if (!rt.contains(name)) iterator.remove()
//        }
    }

    private fun getTrack(tracks: List<Track>, track: String): Track? {
        for (t in tracks) {
            if (t.name == track) {
                return t
            }
        }
        return null
    }


    private fun initSessionIds(sessions: List<Session>) {
        val ss = ArrayList(sessions)
        sessIds = ArrayList()
        for (i in ss.indices) {
            sessIds.add(i)
        }
    }

    private fun createSpeakerName(session: Session): String {
        return if (session.speakerIds == null || session.speakerIds!!.isEmpty()) "" else Joiner.on(", ").join(session.speakerIds!!)
    }

    private fun displayEventDetails(id: String) {
        findNavController().navigate(AgendaFragmentDirections.showSessionInfo(id))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //first saving my state, so the bundle wont be empty.
        //http://code.google.com/p/android/issues/detail?id=19917
        outState.putLong("something", 1)
        super.onSaveInstanceState(outState)
    }

    companion object {
        private val SESSION_BY_DATE_COMPARATOR = Comparator<Session> { lhs: Session, rhs: Session -> lhs.startTime!!.compareTo(rhs.startTime!!) }
    }
}
