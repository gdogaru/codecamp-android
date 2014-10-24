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

package com.gdogaru.codecamp.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.db.DatabaseHelper;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.view.sessions.SessionListItem;
import com.gdogaru.codecamp.view.sessions.SessionsAdapter;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */

public class SessionsTabFragment extends Fragment {

    private static final String TRACK_ID = "TrackId";
    final private AdapterView.OnItemClickListener mOnClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onListItemClick((ListView) parent, v, position, id);
        }
    };
    StickyListHeadersListView listView;
    RelativeLayout trackSelectorLayout;
    TrackSelectorFragment trackSelector;
    private DatabaseHelper dbHelper;
    private List<Track> tracks;
    private SessionsAdapter sessionsAdapter;
    private ArrayList<SessionListItem> sessionListItems = null;
    private long trackId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sessions_fragment, container, false);
        trackSelectorLayout = (RelativeLayout) view.findViewById(R.id.trackSelectorLayout);
        listView = (StickyListHeadersListView) view.findViewById(android.R.id.list);
        initView();
        initTrackSelector();
        return view;
    }

    public void initView() {
        dbHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        tracks = dbHelper.getTrackDao().queryForAll();
        listView.setOnItemClickListener(mOnClickListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(TRACK_ID)) {
            trackId = savedInstanceState.getLong(TRACK_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListData(trackId);
    }

    private void initTrackSelector() {
        List<Track> trackList = new ArrayList<Track>();
        Track track = new Track();
        track.setName(getString(R.string.all_tracks));
        trackList.add(track);
        trackList.addAll(tracks);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        trackSelector = new TrackSelectorFragment();
        transaction.add(R.id.trackSelectorLayout, trackSelector).commit();

        trackSelector.setTrackList(trackList);
        trackSelector.setCurrentTrack(trackId);
        trackSelector.setTrackSelectedListener(new TrackSelectorFragment.TrackSelectedListener() {
            @Override
            public void onTrackSelected(Track track) {
                refreshListData(track == null ? 0L : track.getId());
            }
        });
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        SessionListItem session = (SessionListItem) sessionsAdapter.getItem(position);
        ExpandedSessionsInfoActivity.start(getActivity(),
                session.getId(),
                trackId,
                getSessionIds());
    }

    private ArrayList<Integer> getSessionIds() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (SessionListItem session : sessionsAdapter.getSessions()) {
            result.add((int) session.getId());
        }
        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OpenHelperManager.releaseHelper();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(TRACK_ID, trackId);
        super.onSaveInstanceState(outState);
    }


    void refreshListData(long trackId) {
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
        Date currentTime = cal.getTime();
        for (int i = 0; i < currentSessions.size(); i++) {
            long timeDiff = currentSessions.get(i).getStart().getTime() - currentTime.getTime();
            if (timeDiff >= 0) {
                return i - 1;
            }
        }
        return -1;
    }

    private List<SessionListItem> getFilterSessions(long trackId) {
        List<SessionListItem> result = new ArrayList<SessionListItem>();
        for (SessionListItem item : sessionListItems) {
            if (item.getTrackId() == null || trackId == 0 || item.getTrackId() == trackId) {
                result.add(item);
            }
        }
        return result;
    }

    private void loadSessions() {
        RuntimeExceptionDao<Session, Long> dao = dbHelper.getSessionDao();
        List<Session> sessions = dao.queryForAll();
        List<Speaker> speakers = dbHelper.getSpeakerDao().queryForAll();

        Map<Long, String> idToSpeaker = SessionsUtil.extractSpeakers(speakers);
        Map<Long, String> idToTrack = SessionsUtil.extractTrackDisplay(tracks);

        sessionListItems = new ArrayList<SessionListItem>();
        for (Session s : sessions) {
            SessionListItem item = new SessionListItem();
            item.setName(s.getTitle());
            item.setId(s.getId());
            item.setEnd(s.getEnd());
            item.setStart(s.getStart());
            item.setTrackId(s.getTrackRefId());
            if (s.getTrackRefId() != null) {
                item.setTrackName(idToTrack.get(s.getTrackRefId()));
            }
            List<String> speakerNames = new ArrayList<String>();
            for (long speakerId : s.getSpeakerRefIds()) {
                speakerNames.add(idToSpeaker.get(speakerId));
            }
            item.setSpeakerNames(speakerNames);
            sessionListItems.add(item);
        }
    }
}
