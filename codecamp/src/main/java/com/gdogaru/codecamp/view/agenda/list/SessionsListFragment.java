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

package com.gdogaru.codecamp.view.agenda.list;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Schedule;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.BookmarkingService;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.Strings;
import com.gdogaru.codecamp.view.agenda.SessionsFragment;
import com.gdogaru.codecamp.view.session.SessionExpandedActivity;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static android.R.id.list;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */

public class SessionsListFragment extends SessionsFragment {
    private static final String LIST_STATE = "LIST_STATE";
    @BindView(list)
    StickyListHeadersListView listView;
    //    @BindView(R.id.spinner)
//    Spinner trackSelector;
    @Inject
    CodecampClient codecampClient;
    @Inject
    BookmarkingService bookmarkingService;
    @State
    String trackId;
    @State
    Parcelable listState;
    private List<Track> tracks;
    private SessionsAdapter sessionsAdapter;
    final private AdapterView.OnItemClickListener mOnClickListener
            = (parent, v, position, id) -> onListItemClick((ListView) parent, v, position, id);
    private ArrayList<SessionListItem> sessionListItems = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getDiComponent().inject(this);
        tryGetListState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agenda_sessions_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manage(ButterKnife.bind(this, view));

        initView();
        initTrackSelector();

        updateDisplay();
    }

    public void initView() {
        Schedule schedule = codecampClient.getSchedule();
        tracks = schedule.getTracks();
        listView.setOnItemClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
       if(sessionsAdapter!= null) sessionsAdapter.setBookmarked(bookmarkingService.getBookmarked(codecampClient.getEvent().getTitle()));
    }

    private void initTrackSelector() {
        List<String> trackList = new ArrayList<>();
        trackList.add(getString(R.string.all_tracks));
        for (Track t : tracks) {
            trackList.add(t.getName());
        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, trackList);
//        trackSelector.setAdapter(adapter);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        SessionListItem session = (SessionListItem) sessionsAdapter.getItem(position);
        SessionExpandedActivity.start(getActivity(),
                session.getId(),
                trackId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        tryGetListState(savedInstanceState);
    }

    private void tryGetListState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(LIST_STATE)) {
            listState = savedInstanceState.getParcelable(LIST_STATE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE, listView.onSaveInstanceState());
    }

    public void updateDisplay() {
        if (sessionListItems == null) {
            loadSessions();
        }
        List<SessionListItem> currentSessions = getTrackSessions(trackId);
        Set<String> bookmarked = bookmarkingService.getBookmarked(codecampClient.getEvent().getTitle());
        if (getFavoritesOnly()) {
            currentSessions = extractFavorites(currentSessions, bookmarked);
        }
        sessionsAdapter = new SessionsAdapter(getActivity(), currentSessions, bookmarked);
        listView.setAdapter(sessionsAdapter);
        if (listState != null) {
            listView.onRestoreInstanceState(listState);
        }
    }

    private int findNext(List<SessionListItem> currentSessions) {
        java.util.Calendar cal = GregorianCalendar.getInstance();
//        cal.set(2014, java.util.Calendar.OCTOBER, 25, 16, 45, 10);
//        Date currentTime = cal.getTime();
//        for (int i = 0; i < currentSessions.size(); i++) {
//            long timeDiff = currentSessions.get(i).getStart().getTime() - currentTime.getTime();
//            if (timeDiff >= 0) {
//                return i - 1;
//            }
//        }
        return -1;
    }

    protected List<SessionListItem> extractFavorites(List<SessionListItem> currentSessions, Set<String> bookmarked) {
        ArrayList<SessionListItem> result = new ArrayList<>();
        for (SessionListItem li : currentSessions) {
            if (bookmarked.contains(li.getId())) result.add(li);
        }
        return result;
    }

    private List<SessionListItem> getTrackSessions(String trackId) {
        List<SessionListItem> result = new ArrayList<>();
        for (SessionListItem item : sessionListItems) {
            if (Strings.isNullOrEmpty(item.getTrackName()) || Strings.isNullOrEmpty(trackId) || item.getTrackName().equals(trackId)) {
                result.add(item);
            }
        }
        return result;
    }

    private void loadSessions() {
        Schedule schedule = codecampClient.getSchedule();
        List<Session> sessions = schedule.getSessions();

        sessionListItems = new ArrayList<>();
        if (sessions == null) return;

        for (Session s : sessions) {
            SessionListItem item = new SessionListItem();
            item.setName(s.getTitle());
            item.setId(s.getId());
            item.setEnd(s.getEndTime());
            item.setStart(s.getStartTime());
            item.setTrackName(s.getTrack());
            if (s.getSpeakerIds() != null) {
                item.setSpeakerNames(new ArrayList<>(s.getSpeakerIds()));
            } else {
                item.setSpeakerNames(new ArrayList<>());
            }
            sessionListItems.add(item);
        }
    }
}
