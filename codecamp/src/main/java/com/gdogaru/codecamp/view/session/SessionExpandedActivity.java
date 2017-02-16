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

package com.gdogaru.codecamp.view.session;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.BookmarkingService;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.Strings;
import com.gdogaru.codecamp.view.BaseActivity;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnItemSelected;

import static org.slf4j.LoggerFactory.getLogger;

public class SessionExpandedActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private static final String TRACK_ID = "trackID";
    private static final String SESSION_ID = "sessionId";
    private static final Logger LOG = getLogger(SessionExpandedActivity.class);
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.track_spinner)
    Spinner trackSpinner;
    @BindView(R.id.bookmarked)
    CheckBox bookmarked;
    @Inject
    CodecampClient codecampClient;
    @Inject
    BookmarkingService bookmarkingService;
    String sessionId;
    String trackId;
    ArrayList<String> trackSessions;
    private List<Track> trackList;
    private String allTracksString;
    private ExpandedSessionsAdapter adapter;

    public static void start(Activity activity, String id, String trackId) {
        Intent intent = new Intent(activity, SessionExpandedActivity.class);
        intent.putExtra(SESSION_ID, id);
        intent.putExtra(TRACK_ID, trackId);

        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.act_right_left, R.anim.act_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getDiComponent().inject(this);

        restoreState(savedInstanceState);
        setContentView(R.layout.session_expanded_activity);
        ButterKnife.bind(this);

        allTracksString = getString(R.string.all_tracks);

        initViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void initViews() {
        initSelector();
        initPager();
    }

    private void initPager() {
        trackSessions = codecampClient.getTrackSesssionsIds(allTracksString.equals(trackId) ? null : trackId);

        adapter = new ExpandedSessionsAdapter(getSupportFragmentManager(), getLayoutInflater(), trackSessions);
        int index = Iterables.indexOf(trackSessions, input -> input.equals(sessionId));
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(index < 0 ? 0 : index);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SESSION_ID)) {
                sessionId = savedInstanceState.getString(SESSION_ID);
            }
            if (savedInstanceState.containsKey(TRACK_ID)) {
                trackId = savedInstanceState.getString(TRACK_ID);
            }
        } else if (getIntent().getExtras().size() > 0) {
            if (getIntent().hasExtra(SESSION_ID)) {
                sessionId = getIntent().getStringExtra(SESSION_ID);
            }
            if (getIntent().hasExtra(TRACK_ID)) {
                trackId = getIntent().getStringExtra(TRACK_ID);
            }
        }
        if (Strings.isNullOrEmpty(trackId)) trackId = allTracksString;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SESSION_ID, sessionId);
        outState.putString(TRACK_ID, trackId);
    }

    private void initSelector() {
        trackList = codecampClient.getSchedule().getTracks();
        Track track = new Track();
        track.setName(allTracksString);
        trackList.add(0, track);
        List<String> trackNames = Lists.newArrayList(Iterables.transform(trackList, input -> input.getName()));
        int position = trackNames.indexOf(trackId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, trackNames);
        adapter.setDropDownViewResource(R.layout.dropdown_item_drop);
        trackSpinner.setAdapter(adapter);
        trackSpinner.setSelection(position);
    }

    @OnItemSelected(R.id.track_spinner)
    public void onTrackSelected(Spinner spinner, int position) {
        String newTrackId = trackList.get(position).getName();

        if (!Strings.nullToEmpty(newTrackId).equals(trackId)) {
            trackId = newTrackId;
            initPager();
        }
    }

    @OnCheckedChanged(R.id.bookmarked)
    public void onBookmarkChanged(boolean checked) {
        bookmarkingService.setBookmarked(codecampClient.getEvent().getTitle(), adapter.getElement(viewPager.getCurrentItem()), checked);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        String s = adapter.getElement(position);
        bookmarked.setChecked(bookmarkingService.isBookmarked(codecampClient.getEvent().getTitle(), s));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ExpandedSessionsAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<String> trackSessions;
        LayoutInflater layoutInflater;

        public ExpandedSessionsAdapter(FragmentManager fm, LayoutInflater layoutInflater, ArrayList<String> trackSessions) {
            super(fm);
            this.layoutInflater = layoutInflater;
            this.trackSessions = trackSessions;
            LOG.trace("Set sessions: {}", trackSessions);
        }

        @Override
        public Fragment getItem(int position) {
            return SessionInfoFragment.newInstance(trackSessions.get(position));
        }

        @Override
        public int getCount() {
            return trackSessions.size();
        }

        public String getElement(int idx) {
            return trackSessions.get(idx);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
//            SessionInfoFragment_ fragment_ = (SessionInfoFragment_) object;
//            int position = trackSessions.indexOf(fragment_.getSessionId().intValue());
//            return position == -1 ? POSITION_NONE : position;
        }

    }
}
