package com.gdogaru.codecamp.view.calendar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.gdogaru.codecamp.db.DatabaseHelper;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.view.ExpandedSessionsInfoActivity;
import com.gdogaru.codecamp.view.SessionsUtil;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CalendarFragment extends Fragment {

    private static final Comparator<? super Session> SESSION_BY_DATE_COMPARATOR = new Comparator<Session>() {
        @Override
        public int compare(Session lhs, Session rhs) {
            return lhs.getStart().compareTo(rhs.getStart());
        }
    };

    ScrollView scrollView;
    ArrayList<Integer> sessIds = new ArrayList<Integer>();
    private int offset;
    private Calendar calendar;
    Timer currentTimer;

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
        DatabaseHelper dbHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);

        List<Session> sessions = dbHelper.getSessionDao().queryForAll();
        List<Track> tracks = dbHelper.getTrackDao().queryForAll();
        List<Speaker> speakers = dbHelper.getSpeakerDao().queryForAll();

        Map<Long, String> idToSpeaker = SessionsUtil.extractSpeakers(speakers);
        Map<Long, String> idToTrack = SessionsUtil.extractTrackDisplay(tracks);

        List<CEvent> events = new ArrayList<CEvent>();
        Collections.sort(sessions, SESSION_BY_DATE_COMPARATOR);
        int startDay = getDay(sessions.get(0).getStart());
        for (Session ss : sessions) {
            if (getDay(ss.getStart()) - startDay == offset) {
                int preferedIdx = ss.getTrackRefId() == null ? 0 : ss.getTrackRefId().intValue();
                String descLine2 = idToTrack.get(ss.getTrackRefId());
                events.add(new CEvent(ss.getId(), ss.getStart(), ss.getEnd(), preferedIdx, ss.getTitle(),
                        createSpeakerName(ss.getSpeakerRefIds(), idToSpeaker),
                        descLine2 == null ? "" : descLine2));
            }

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

    private int getDay(Date date) {
        java.util.Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        return cal.get(java.util.Calendar.DAY_OF_YEAR);
    }

    private void initSessionIds(List<Session> sessions) {
        List<Session> ss = new ArrayList<Session>(sessions);
        Collections.sort(ss, Session.SESSION_BY_DATE_COMPARATOR);
        sessIds = new ArrayList<Integer>();
        for (Session s : ss) {
            sessIds.add((int) s.getId());
        }
    }

    private String createSpeakerName(Long[] descLine1, Map<Long, String> idsToSpeaker) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < descLine1.length; i++) {
            Long id = descLine1[i];
            if (id == null) {
                continue; //retarded api
            }
            String str = idsToSpeaker.get(id);
            if (str != null) {
                if (i > 0) {
                    result.append(", ");
                }
                result.append(str);
            }
        }
        return result.toString();
    }

    private void displayEventDetails(long id) {
        ExpandedSessionsInfoActivity.start(getActivity(), id, 0L, sessIds);
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
        OpenHelperManager.releaseHelper();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
