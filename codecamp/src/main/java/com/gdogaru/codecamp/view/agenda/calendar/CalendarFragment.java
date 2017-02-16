package com.gdogaru.codecamp.view.agenda.calendar;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.model.Codecamp;
import com.gdogaru.codecamp.model.Schedule;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.view.session.SessionExpandedActivity;
import com.google.common.base.Joiner;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class CalendarFragment extends Fragment {

    private static final Comparator<? super Session> SESSION_BY_DATE_COMPARATOR = new Comparator<Session>() {
        @Override
        public int compare(Session lhs, Session rhs) {
            return lhs.getStartTime().compareTo(rhs.getStartTime());
        }
    };

    ScrollView scrollView;
    ArrayList<Integer> sessIds = new ArrayList<Integer>();
    Timer currentTimer;
    @Inject
    CodecampClient codecampClient;
    private int offset;
    private Calendar calendar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getDiComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        ZoomView zv = new ZoomView(getActivity());
        calendar = new Calendar(getActivity());
//        zv.addView(calendar);
        return calendar;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        calendar = (Calendar) view;
//        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        currentTimer = new Timer();
        currentTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                java.util.Calendar cal = GregorianCalendar.getInstance();
//                cal.set(2014, java.util.Calendar.OCTOBER, 25, 17, 25, 10);
                final Date currentTime = cal.getTime();
                calendar.post(new Runnable() {
                    @Override
                    public void run() {
                        calendar.updateCurrentTime(currentTime);
                    }
                });
            }
        }, 10000, 10000);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (currentTimer != null) {
            currentTimer.cancel();
        }
    }

    public void init() {
        Schedule schedule = codecampClient.getSchedule();
        Codecamp codecamp = codecampClient.getEvent();
        List<Session> sessions = schedule.getSessions();
        List<Track> tracks = schedule.getTracks();
        List<Speaker> speakers = codecamp.getSpeakers();

        List<CEvent> events = new ArrayList<CEvent>();
        Collections.sort(sessions, SESSION_BY_DATE_COMPARATOR);
        int startDay = getDay(schedule.getDate());
        for (int i = 0; i < sessions.size(); i++) {
            Session ss = sessions.get(i);
            int preferedIdx = ss.getTrack() == null ? 0 : getTrack(tracks, ss.getTrack()).getDisplayOrder();
            String descLine2 = ss.getTrack();
            events.add(new CEvent(ss.getId(), ss.getStartTime(), ss.getEndTime(), preferedIdx, ss.getTitle(),
                    createSpeakerName(ss),
                    descLine2 == null ? "" : descLine2));
        }
        initSessionIds(sessions);

        java.util.Calendar cal = GregorianCalendar.getInstance();
//        cal.set(2014, java.util.Calendar.OCTOBER, 25, 17, 5, 10);
        Date currentTime = cal.getTime();

//        Calendar calendar = new Calendar(getActivity());
        calendar.setCurrentTime(currentTime);
        calendar.setEvents(events);

//        ViewGroup.LayoutParams lp = calendar.getLayoutParams();
//        if (lp == null) {
//            lp = new ViewGroup.LayoutParams(getActivity().getWindowManager().getDefaultDisplay().getWidth(), getActivity().getWindowManager().getDefaultDisplay().getHeight());
//        }
//        calendar.setLayoutParams(lp);


//        scrollView.addView(calendar);
        calendar.setEventListener(new Calendar.EventListener() {
            @Override
            public void eventCLicked(DisplayEvent event) {
                displayEventDetails(event.event.id);
            }
        });

//        GestureDetector gd = new GestureDetector(this,new OnPinchListener(scrollView));

    }

    private Track getTrack(List<Track> tracks, String track) {
        for (Track t : tracks) {
            if (t.getName().equals(track)) {
                return t;
            }
        }
        return null;
    }

    private int getDay(LocalDateTime date) {
        return date.getDayOfYear();
    }

    private void initSessionIds(List<Session> sessions) {
        List<Session> ss = new ArrayList<Session>(sessions);
//        Collections.sort(ss, Session.SESSION_BY_DATE_COMPARATOR);
        sessIds = new ArrayList<Integer>();
        for (int i = 0; i < ss.size(); i++) {
            sessIds.add((int) i);
        }
    }

    private String createSpeakerName(Session session) {
        if (session.getSpeakerIds() == null || session.getSpeakerIds().isEmpty()) return "";
        return Joiner.on(", ").join(session.getSpeakerIds());
    }

    private void displayEventDetails(String id) {
        SessionExpandedActivity.start(getActivity(), id, null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        //http://code.google.com/p/android/issues/detail?id=19917
        outState.putLong("something", 1);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
