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

package com.gdogaru.codecamp.view.speaker;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.view.BaseActivity;
import com.google.common.collect.Iterables;

import org.slf4j.Logger;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.slf4j.LoggerFactory.getLogger;

public class SpeakerExpandedActivity extends BaseActivity {

    private static final String SPEAKER_ID = "speakerId";
    private static final Logger LOG = getLogger(SpeakerExpandedActivity.class);
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
    String speakerId;

    public static void start(Activity activity, String id) {
        Intent intent = new Intent(activity, SpeakerExpandedActivity.class);
        intent.putExtra(SPEAKER_ID, id);

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
        trackSpinner.setVisibility(View.GONE);
        bookmarked.setVisibility(View.GONE);

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
        initPager();
    }

    private void initPager() {
        List<Speaker> speakers = codecampClient.getEvent().getSpeakers();
        SpeakerAdapter adapter = new SpeakerAdapter(getSupportFragmentManager(), getLayoutInflater(), speakers);
        int index = Iterables.indexOf(speakers, input -> input != null && input.getName() != null && input.getName().equals(speakerId));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(index < 0 ? 0 : index);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SPEAKER_ID)) {
                speakerId = savedInstanceState.getString(SPEAKER_ID);
            }

        } else if (getIntent().getExtras().size() > 0) {
            if (getIntent().hasExtra(SPEAKER_ID)) {
                speakerId = getIntent().getStringExtra(SPEAKER_ID);
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SPEAKER_ID, speakerId);
    }


    private class SpeakerAdapter extends FragmentStatePagerAdapter {

        private final List<Speaker> trackSessions;
        LayoutInflater layoutInflater;

        public SpeakerAdapter(FragmentManager fm, LayoutInflater layoutInflater, List<Speaker> trackSessions) {
            super(fm);
            this.layoutInflater = layoutInflater;
            this.trackSessions = trackSessions;
            LOG.trace("Set sessions: {}", trackSessions);
        }

        @Override
        public Fragment getItem(int position) {
            return SpeakerInfoFragment.newInstance(trackSessions.get(position));
        }

        @Override
        public int getCount() {
            return trackSessions.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
