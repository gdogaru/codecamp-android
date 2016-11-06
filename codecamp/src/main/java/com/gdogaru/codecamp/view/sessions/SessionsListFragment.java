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

package com.gdogaru.codecamp.view.sessions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Schedule;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.Strings;
import com.gdogaru.codecamp.view.session.SessionExpandedActivity;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */

public class SessionsListFragment extends Fragment {

    private static final String TRACK_ID = "TrackId";
    @BindView(android.R.id.list)
    StickyListHeadersListView listView;
    @BindView(R.id.spinner)
    Spinner trackSelector;
    @Inject
    CodecampClient codecampClient;
    private List<Track> tracks;
    private SessionsAdapter sessionsAdapter;
    private ArrayList<SessionListItem> sessionListItems = null;
    private String trackId;
    final private AdapterView.OnItemClickListener mOnClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onListItemClick((ListView) parent, v, position, id);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getDiComponent().inject(this);
        if (savedInstanceState != null && savedInstanceState.containsKey(TRACK_ID)) {
            trackId = savedInstanceState.getString(TRACK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sessions_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        trackId = savedInstanceState == null ? null : savedInstanceState.getString(TRACK_ID);
        initView();
        initTrackSelector();
    }

    public void initView() {
        Schedule schedule = codecampClient.getSchedule();
        tracks = schedule.getTracks();
        listView.setOnItemClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListData(trackId);
    }

    private void initTrackSelector() {
        List<String> trackList = new ArrayList<>();
        trackList.add(getString(R.string.all_tracks));
        for (Track t : tracks) {
            trackList.add(t.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.sessions_fragment_spitem, trackList);
        trackSelector.setAdapter(adapter);
    }

    @OnItemSelected(R.id.spinner)
    public void onTrackSelected(Spinner spinner, int position) {
        String newTrack = position == 0 ? null : (String) trackSelector.getAdapter().getItem(position);
        if ((newTrack == null && trackId != null) || (newTrack != null && !newTrack.equals(trackId))) {
            trackId = newTrack;
            refreshListData(trackId);
        }
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
        trackId = savedInstanceState == null ? null : savedInstanceState.getString(TRACK_ID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TRACK_ID, trackId);
        super.onSaveInstanceState(outState);
    }

    void refreshListData(String trackId) {
        if (sessionListItems == null) {
            loadSessions();
        }
        this.trackId = trackId;
        List<SessionListItem> currentSessions = getFilterSessions(trackId);
        sessionsAdapter = new SessionsAdapter(getActivity(), currentSessions);
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(sessionsAdapter);
            }
        });
        final int position = findNext(currentSessions);
        if (position > 0) {
            listView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.setSelection(position);
                }
            }, 300);
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

    private List<SessionListItem> getFilterSessions(String trackId) {
        List<SessionListItem> result = new ArrayList<SessionListItem>();
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

        sessionListItems = new ArrayList<SessionListItem>();
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
