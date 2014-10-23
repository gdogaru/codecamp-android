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


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;

import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.db.DatabaseHelper;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Track;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpandedSessionsInfoActivity extends CodecampActivity {

    private static final String TRACK_ID = "trackID";
    private static final String SESSION_ID = "sessionId";
    private static final String TRACK_SESSIONS = "trackSessions";

    TrackSelectorFragment trackSelector;
    ViewPager viewPager;
    Long sessionId;
    Long trackId;
    ArrayList<Integer> trackSessions;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreState(savedInstanceState);
        setContentView(R.layout.expanded_sessions);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        trackSelector = (TrackSelectorFragment) getFragmentManager().findFragmentById(R.id.trackSelector);

        initViews();
        setChildActionBar(R.string.session_info);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public void initViews() {
        dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

        ExpandedSessionsAdapter adapter = new ExpandedSessionsAdapter(getFragmentManager(), getLayoutInflater(), trackSessions);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(trackSessions.indexOf(sessionId.intValue()));
        initSelector();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SESSION_ID)) {
                sessionId = savedInstanceState.getLong(SESSION_ID);
            }
            if (savedInstanceState.containsKey(TRACK_ID)) {
                trackId = savedInstanceState.getLong(TRACK_ID);
            }
            if (savedInstanceState.containsKey(TRACK_SESSIONS)) {
                trackSessions = savedInstanceState.getIntegerArrayList(TRACK_SESSIONS);
            } else {
                trackSessions = new ArrayList<Integer>();
            }
        } else if (getIntent().getExtras().size() > 0) {
            if (getIntent().hasExtra(SESSION_ID)) {
                sessionId = getIntent().getLongExtra(SESSION_ID, -1);
            }
            if (getIntent().hasExtra(TRACK_ID)) {
                trackId = getIntent().getLongExtra(TRACK_ID, -1);
            }
            if (getIntent().hasExtra(TRACK_SESSIONS)) {
                trackSessions = getIntent().getIntegerArrayListExtra(TRACK_SESSIONS);
            } else {
                trackSessions = new ArrayList<Integer>();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SESSION_ID, sessionId);
        outState.putLong(TRACK_ID, trackId);
        outState.putIntegerArrayList(TRACK_SESSIONS, trackSessions);
    }

    private void initSelector() {
        List<Track> tracks = dbHelper.getTrackDao().queryForAll();
        Track track = new Track();
        track.setName(getString(R.string.all_tracks));
        tracks.add(0, track);
        trackSelector.setTrackList(tracks);
        trackSelector.setCurrentTrack(trackId);
        trackSelector.setTrackSelectedListener(new TrackSelectorFragment.TrackSelectedListener() {
            @Override
            public void onTrackSelected(Track track) {
                changeTracks(track);
            }
        });
        trackSelector.init();
    }

    void changeTracks(Track track) {
        List<Session> sessions = null;
        try {
            if (track == null || track.getId() == 0) {
                sessions = dbHelper.getSessionDao().queryForAll();
            } else {
                QueryBuilder<Session, Long> builder = dbHelper.getSessionDao().queryBuilder();
                Where<Session, Long> where = builder.where();
                sessions = where.or(where.isNull("trackRefId"), where.eq("trackRefId", track.getId())).query();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final ArrayList<Integer> ids = new ArrayList<Integer>();
        Collections.sort(sessions, Session.SESSION_BY_DATE_COMPARATOR);
        for (Session s : sessions) {
            ids.add((int) s.getId());
        }
        trackSessions.clear();
        trackSessions.addAll(ids);

        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setAdapter(new ExpandedSessionsAdapter(getFragmentManager(), getLayoutInflater(), ids));
                viewPager.setCurrentItem(0);
            }
        });
    }

    public static void start(Activity activity, long id, long trackId, ArrayList<Integer> trackSessions) {
        Intent intent = new Intent(activity, ExpandedSessionsInfoActivity.class);
        intent.putExtra(SESSION_ID, id);
        intent.putExtra(TRACK_ID, trackId);
        intent.putExtra(TRACK_SESSIONS, trackSessions);
        activity.startActivity(intent);
    }

    private class ExpandedSessionsAdapter extends FragmentStatePagerAdapter {

        LayoutInflater layoutInflater;
        private final ArrayList<Integer> trackSessions;

        public ExpandedSessionsAdapter(FragmentManager fm, LayoutInflater layoutInflater, ArrayList<Integer> trackSessions) {
            super(fm);
            this.layoutInflater = layoutInflater;
            this.trackSessions = trackSessions;
        }

        @Override
        public Fragment getItem(int position) {
            long sessionId = trackSessions.get(position);
            SessionInfoFragment sessionInfoFragment = new SessionInfoFragment();
            sessionInfoFragment.setSessionId(sessionId);
            return sessionInfoFragment;
        }

        @Override
        public int getCount() {
            return trackSessions.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
//            SessionInfoFragment_ fragment_ = (SessionInfoFragment_) object;
//            int position = trackSessions.indexOf(fragment_.getSessionId().intValue());
//            return position == -1 ? POSITION_NONE : position;
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OpenHelperManager.releaseHelper();
    }


}
