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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Codecamp;
import com.gdogaru.codecamp.model.Schedule;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.BookmarkingService;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.DateUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class SpeakerInfoFragment extends Fragment {
    public static final String SPEAKER_ID = "speakerId";
    String speakerId;
    @Inject
    CodecampClient codecampClient;
    @Inject
    BookmarkingService bookmarkingService;
    @Inject
    FirebaseAnalytics firebaseAnalytics;
    private LinearLayout rootView;

    public static SpeakerInfoFragment newInstance(Speaker speaker) {
        SpeakerInfoFragment sessionInfoFragment = new SpeakerInfoFragment();
        sessionInfoFragment.setArguments(new Bundle());
        sessionInfoFragment.getArguments().putString(SPEAKER_ID, speaker.getName());
        return sessionInfoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getDiComponent().inject(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(SPEAKER_ID)) {
            speakerId = savedInstanceState.getString(SPEAKER_ID);
        } else {
            speakerId = getArguments().getString(SPEAKER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frame_vertical, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        rootView = (LinearLayout) view.findViewById(R.id.content);
        initView();
    }

    public void initView() {
        Speaker speaker = codecampClient.getSpeaker(speakerId);
        if (speaker == null) return;

        rootView.addView(addSpeaker(speaker));
        List<Pair<Codecamp, Session>> sessions = codecampClient.getSessionsBySpeaker(speakerId);
        for (Pair<Codecamp, Session> session : sessions) {
            rootView.addView(addSession(session.second, session.first));
        }

        Bundle bundle = new Bundle();
        bundle.putString("speaker", speakerId);
        firebaseAnalytics.logEvent("speaker_view", bundle);
    }

    private View addSession(Session session, Codecamp codecamp) {
        View sessionView = getActivity().getLayoutInflater().inflate(R.layout.session_view, rootView, false);
        TextView sessionTitle = (TextView) sessionView.findViewById(R.id.sessionTitle);
        TextView sessionTime = (TextView) sessionView.findViewById(R.id.sessionTime);
        TextView sessionDescription = (TextView) sessionView.findViewById(R.id.sessionDescription);
        TextView sessionTrack = (TextView) sessionView.findViewById(R.id.sessionTrack);
        CheckBox bookmarked = (CheckBox) sessionView.findViewById(R.id.bookmarked);
        LinearLayout sessionTrackLayout = (LinearLayout) sessionView.findViewById(R.id.sessionTrackLayout);

        sessionTitle.setText(session.getTitle());
        sessionDescription.setText(session.getDescription());
        String timeString = DateUtil.formatPeriod(session.getStartTime(), session.getEndTime());
        sessionTime.setText(timeString);
        bookmarked.setChecked(bookmarkingService.isBookmarked(codecamp.getTitle(), session.getId()));
        bookmarked.setOnCheckedChangeListener((buttonView, isChecked) -> bookmarkingService.setBookmarked(codecamp.getTitle(), session.getId(), isChecked));
        if (session.getTrack() != null) {
            Pair<Track, Schedule> p = codecampClient.getTrackExtended(session.getTrack());
            if (p == null) {
                sessionTrack.setText("");
            } else {
                sessionTrack.setText(String.format(Locale.getDefault(), "%s, %s seats, %s \n%s",
                        p.first.getName(), p.first.getCapacity(), p.first.getDescription(), DateUtil.formatDayOfYear(p.second.getDate())));
            }
        } else {
            sessionTrackLayout.setVisibility(View.GONE);
        }
        return sessionView;
    }

    private View addSpeaker(Speaker speaker) {
        View speakerView = getActivity().getLayoutInflater().inflate(R.layout.session_speaker_info, rootView, false);
        TextView speakerName = (TextView) speakerView.findViewById(R.id.speakerName);
        TextView speakerDesc = (TextView) speakerView.findViewById(R.id.speakerDescription);
        TextView company = (TextView) speakerView.findViewById(R.id.company);
        TextView job = (TextView) speakerView.findViewById(R.id.job_title);
        ImageView picture = (ImageView) speakerView.findViewById(R.id.speakerPhoto);

        speakerName.setText(speaker.getName());
        company.setText(speaker.getCompany());
        job.setText(speaker.getJobTitle());
        speakerDesc.setText(speaker.getBio());

        Glide.with(speakerView.getContext())
                .load(speaker.getPhotoUrl())
                .placeholder(R.drawable.person_icon)
                .centerCrop()
                .crossFade()
                .into(picture);
        return speakerView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SPEAKER_ID, speakerId);
    }
}
