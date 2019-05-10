/*
 * Copyright (C) 2008 Gabriel Dogaru (gdogaru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdogaru.codecamp.view.agenda.list

import android.R.id.list
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import butterknife.BindView
import butterknife.ButterKnife
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Schedule
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.agenda.AgendaFragmentDirections
import com.gdogaru.codecamp.view.agenda.SessionsFragment
import com.gdogaru.codecamp.view.util.autoCleared
import se.emilsjolander.stickylistheaders.StickyListHeadersListView
import java.util.*
import javax.inject.Inject

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */

class SessionsListFragment : SessionsFragment() {
    @BindView(list)
    lateinit var listView: StickyListHeadersListView
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel
    private var sessionsAdapter by autoCleared<SessionsAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tryGetListState(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.agenda_sessions_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manage(ButterKnife.bind(this, view))

        sessionsAdapter = SessionsAdapter(activity!!)
        listView.adapter = sessionsAdapter
        listView.setOnItemClickListener { parent, v, position, id -> onListItemClick(parent as ListView, v, position, id) }

        viewModel.currentSchedule().observe(this, androidx.lifecycle.Observer { s ->
            s?.let { sessionsAdapter.sessions = listItems(it) }
        })
    }

    fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val session = sessionsAdapter.getItem(position) as SessionListItem
        findNavController().navigate(AgendaFragmentDirections.showSessionInfo(session.id))
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        tryGetListState(savedInstanceState)
    }

    private fun tryGetListState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null && savedInstanceState.containsKey(LIST_STATE)) {
//            listState = savedInstanceState.getParcelable(LIST_STATE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(LIST_STATE, listView.onSaveInstanceState())
    }

    override fun updateDisplay() {
        val k = 1;
        //        if (sessionListItems == null) {
        //            loadSessions();
        //        }
        //        List<SessionListItem> currentSessions = getTrackSessions(trackId);
        //        Set<String> bookmarked = bookmarkingService.getBookmarked(codecampClient.getEvent().getTitle());
        //        if (getFavoritesOnly()) {
        //            currentSessions = extractFavorites(currentSessions, bookmarked);
        //        }
        //        sessionsAdapter = new SessionsAdapter(getActivity(), currentSessions, bookmarked);
        //        listView.setAdapter(sessionsAdapter);
        //        if (listState != null) {
        //            listView.onRestoreInstanceState(listState);
        //        }
    }

    private fun findNext(currentSessions: List<SessionListItem>): Int {
        val cal = GregorianCalendar.getInstance()
        //        cal.set(2014, java.util.Calendar.OCTOBER, 25, 16, 45, 10);
        //        Date currentTime = cal.getTime();
        //        for (int i = 0; i < currentSessions.size(); i++) {
        //            long timeDiff = currentSessions.get(i).getStart().getTime() - currentTime.getTime();
        //            if (timeDiff >= 0) {
        //                return i - 1;
        //            }
        //        }
        return -1
    }

    protected fun extractFavorites(currentSessions: List<SessionListItem>, bookmarked: Set<String>): List<SessionListItem> {
        val result = ArrayList<SessionListItem>()
        for (li in currentSessions) {
            if (bookmarked.contains(li.id)) result.add(li)
        }
        return result
    }
//
//    private fun getTrackSessions(trackId: String, it: Codecamp): List<SessionListItem> {
//        val result = ArrayList<SessionListItem>()
//        for (item in sessionListItems!!) {
//            if (Strings.isNullOrEmpty(item.trackName) || Strings.isNullOrEmpty(trackId) || item.trackName == trackId) {
//                result.add(item)
//            }
//        }
//        return result
//    }

    private fun listItems(schedule: Schedule): List<SessionListItem> {
        val sessions = schedule.sessions.orEmpty()
        val sessionListItems = mutableListOf<SessionListItem>()

        for (s in sessions) {
            val item = SessionListItem()
            item.name = s.title
            item.id = s.id
            item.end = s.endTime
            item.start = s.startTime
            item.trackName = s.track
            item.speakerNames = ArrayList(s.speakerIds.orEmpty())
            sessionListItems.add(item)
        }
        return sessionListItems
    }

    companion object {
        private val LIST_STATE = "LIST_STATE"
    }
}
