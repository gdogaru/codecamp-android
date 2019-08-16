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
import com.gdogaru.codecamp.di.Injectable
import com.gdogaru.codecamp.view.agenda.AgendaFragmentDirections
import com.gdogaru.codecamp.view.agenda.AbstractSessionsListFragment
import com.gdogaru.codecamp.view.agenda.calendar.component.CEvent
import com.gdogaru.codecamp.view.agenda.calendar.component.Calendar
import com.gdogaru.codecamp.view.agenda.calendar.component.DisplayEvent
import com.gdogaru.codecamp.view.agenda.calendar.component.EventListener
import org.threeten.bp.LocalDateTime
import java.util.*
import javax.inject.Inject


class CalendarFragment : AbstractSessionsListFragment(), Injectable {

    private var sessIds = ArrayList<Int>()
    private var currentTimer: Timer? = null
    private val offset: Int = 0
    private lateinit var calendar: Calendar
    @State
    lateinit var calendarState: Calendar.CalendarState
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CalendarFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(CalendarFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        calendar = Calendar(requireActivity())
        return calendar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.eventList().observe(this, androidx.lifecycle.Observer { s ->
            s?.let { updateDisplay(it.first, it.second) }
        })
    }


    override fun onResume() {
        super.onResume()
        currentTimer = Timer()
        currentTimer!!.schedule(object : TimerTask() {
            override fun run() {
                calendar.post { calendar.updateCurrentTime(LocalDateTime.now()) }
            }
        }, 500, 30000)
    }

    override fun onPause() {
        super.onPause()
        if (currentTimer != null) {
            currentTimer!!.cancel()
        }
        calendarState = calendar.state
    }

    override fun setFavoritesOnly(favoritesOnly: Boolean) {
        viewModel.setFavoritesOnly(favoritesOnly)
    }

    private fun updateDisplay(events: List<CEvent>, schedule: Schedule) {
        initSessionIds(schedule.sessions)
        calendar.setCurrentTime(LocalDateTime.now())
        calendar.setEvents(events)
        calendar.setScheduleDate(schedule.date)

        calendar.setEventListener(object : EventListener {
            override fun eventCLicked(event: DisplayEvent) {
                displayEventDetails(event.event.id)
            }
        })
    }

    private fun initSessionIds(sessions: List<Session>) {
        val ss = ArrayList(sessions)
        sessIds = ArrayList()
        for (i in ss.indices) {
            sessIds.add(i)
        }
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

}
