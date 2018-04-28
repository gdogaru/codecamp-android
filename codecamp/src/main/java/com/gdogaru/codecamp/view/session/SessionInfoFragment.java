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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.di.Injectable;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.DateUtil;
import com.gdogaru.codecamp.view.BaseFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;

public class SessionInfoFragment extends BaseFragment  implements Injectable {

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
    @State
    String sessionId;

    @Inject
    CodecampClient codecampClient;
    @Inject
    FirebaseAnalytics firebaseAnalytics;

    public static SessionInfoFragment newInstance(String id) {
        SessionInfoFragment sessionInfoFragment = new SessionInfoFragment();
        sessionInfoFragment.setArguments(new Bundle());
        sessionInfoFragment.getArguments().putString(SESSION_ID, id);
        return sessionInfoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            sessionId = getArguments().getString(SESSION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session_expanded_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manage(ButterKnife.bind(this, view));

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
            if (track == null) {
                sessionTrack.setText("");
            } else {
                sessionTrack.setText(String.format(Locale.getDefault(), "%s, %s seats, %s", track.getName(), track.getCapacity(), track.getDescription()));
            }
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

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, session.getTitle());
        firebaseAnalytics.logEvent("session_view", bundle);
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

        Glide.with(getActivity())
                .load(speaker.getPhotoUrl())
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.person_icon))
//                .transition(withCrossFade(R.anim.fade_in))
                .into(picture);
    }
}
