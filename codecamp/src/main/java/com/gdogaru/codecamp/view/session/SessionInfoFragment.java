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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.DateUtil;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SessionInfoFragment extends Fragment {

    public static final String SESSION_ID = "sessionId";
    @BindView(R.id.sessionTitle)
    TextView sessionTitle;
    @BindView(R.id.sessionTime)
    TextView sessionTime;
    @BindView(R.id.sessionDescription)
    TextView sessionDescription;
    @BindView(R.id.sessionTrack)
    TextView sessionTrack;
    @BindView(R.id.speakerLayout)
    ViewGroup speakerLayout;
    @BindView(R.id.sessionTrackLayout)
    LinearLayout sessionTrackLayout;
    @BindView(R.id.speakerLayoutOuter)
    ViewGroup speakerLayoutOuter;
    String sessionId;

    @Inject
    CodecampClient codecampClient;

    public static SessionInfoFragment newInstance(String id) {
        SessionInfoFragment sessionInfoFragment = new SessionInfoFragment();
        sessionInfoFragment.setArguments(new Bundle());
        sessionInfoFragment.getArguments().putString(SESSION_ID, id);
        return sessionInfoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getDiComponent().inject(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(SESSION_ID)) {
            sessionId = savedInstanceState.getString(SESSION_ID);
        } else {
            sessionId = getArguments().getString(SESSION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session_expanded_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initView();
    }

    public void initView() {
        Session session = codecampClient.getSession(sessionId);
        sessionTitle.setText(session.getTitle());
        sessionDescription.setText(session.getDescription());
        String timeString = DateUtil.formatPeriod(session.getStartTime(), session.getEndTime());
        sessionTime.setText(timeString);
        if (session.getTrack() != null) {
            Track track = codecampClient.getTrack(session.getTrack());
            sessionTrack.setText(String.format(Locale.getDefault(), "%s, %s seats, %s", track.getName(), track.getCapacity(), track.getDescription()));
        } else {
            sessionTrackLayout.setVisibility(View.GONE);
        }
        if (session.getSpeakerIds() == null || session.getSpeakerIds().size() == 0) {
            speakerLayoutOuter.setVisibility(View.GONE);
        } else {
            for (String id : session.getSpeakerIds()) {
                Speaker speaker = codecampClient.getSpeaker(id);
                if (speaker != null) {
                    addSpeaker(speaker);
                }
            }
        }
    }

    private void addSpeaker(Speaker speaker) {
        View speakerView = getActivity().getLayoutInflater().inflate(R.layout.session_speaker_info, speakerLayout, false);
        TextView speakerName = (TextView) speakerView.findViewById(R.id.speakerName);
        TextView speakerDesc = (TextView) speakerView.findViewById(R.id.speakerDescription);
        TextView company = (TextView) speakerView.findViewById(R.id.company);
        TextView job = (TextView) speakerView.findViewById(R.id.job_title);
        ImageView picture = (ImageView) speakerView.findViewById(R.id.speakerPhoto);

        speakerName.setText(speaker.getName());
        company.setText(speaker.getCompany());
        job.setText(speaker.getJobTitle());
        speakerDesc.setText(speaker.getBio());
        speakerLayout.addView(speakerView);

        Glide.with(speakerView.getContext())
                .load(speaker.getPhotoUrl())
                .placeholder(R.drawable.person_icon)
                .centerCrop()
                .crossFade()
                .into(picture);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SESSION_ID, sessionId);
    }
}
