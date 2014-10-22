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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.db.DatabaseHelper;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.LoadSpeakerPhotoTask;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SessionInfoFragment extends Fragment {

    public static final String SESSION_ID = "sessionId";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
    TextView sessionTitle;
    TextView sessionTime;
    TextView sessionDescription;
    TextView sessionTrack;
    ViewGroup speakerLayout;
    LinearLayout sessionTrackLayout;
    ViewGroup speakerLayoutOuter;
    Long sessionId;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(SESSION_ID)) {
            sessionId = savedInstanceState.getLong(SESSION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session_info,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionTitle = (TextView) view.findViewById(R.id.sessionTitle);
        sessionTime = (TextView) view.findViewById(R.id.sessionTime);
        sessionDescription = (TextView) view.findViewById(R.id.sessionDescription);
        sessionTrack = (TextView) view.findViewById(R.id.sessionTrack);
        speakerLayout = (ViewGroup) view.findViewById(R.id.speakerLayout);
        sessionTrackLayout = (LinearLayout) view.findViewById(R.id.sessionTrackLayout);
        speakerLayoutOuter = (ViewGroup) view.findViewById(R.id.speakerLayoutOuter);

        init();
    }

    public void init() {
        dbHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        initView();
    }

    public void initView() {
        Session session = dbHelper.getSessionDao().queryForId(sessionId);
        sessionTitle.setText(session.getTitle());
        sessionDescription.setText(session.getDescription());
        String timeString = String.format("%s - %s", DATE_FORMAT.format(session.getStart()), DATE_FORMAT.format(session.getEnd()));
        sessionTime.setText(timeString);
        if (!session.isOverrideTracks() && session.getTrackRefId() != null) {
            Track track = dbHelper.getTrackDao().queryForId(session.getTrackRefId());
            sessionTrack.setText(track.getName());
        } else {
            sessionTrackLayout.setVisibility(View.GONE);
        }
        if (session.getSpeakerRefIds().length == 0 || session.getSpeakerRefIds()[0] == -1) {
            speakerLayoutOuter.setVisibility(View.GONE);
        } else {
            for (long id : session.getSpeakerRefIds()) {
                Speaker speaker = dbHelper.getSpeakerDao().queryForId(id);
                if (speaker != null) {
                    addSpeaker(speaker);
                }
            }
        }
    }

    private void addSpeaker(Speaker speaker) {
        View speakerView = getActivity().getLayoutInflater().inflate(R.layout.speaker_info, speakerLayout, false);
        TextView speakerName = (TextView) speakerView.findViewById(R.id.speakerName);
        TextView speakerDesc = (TextView) speakerView.findViewById(R.id.speakerDescription);
        TextView company = (TextView) speakerView.findViewById(R.id.company);
        ImageView picture = (ImageView) speakerView.findViewById(R.id.speakerPhoto);

        speakerName.setText(String.format("%s %s", speaker.getFirstName(), speaker.getLastName() == null ? "" : speaker.getLastName()));
        company.setText(speaker.getCompanyName());
        speakerDesc.setText(speaker.getBio());
        speakerLayout.addView(speakerView);
        new LoadSpeakerPhotoTask(picture, speaker, getActivity()).execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SESSION_ID, sessionId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OpenHelperManager.releaseHelper();
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
