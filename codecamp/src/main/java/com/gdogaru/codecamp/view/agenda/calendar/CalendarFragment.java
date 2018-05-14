package com.gdogaru.codecamp.view.agenda.calendar;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gdogaru.codecamp.di.Injectable;
import com.gdogaru.codecamp.model.Schedule;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.Joiner;
import com.gdogaru.codecamp.view.agenda.SessionsFragment;
import com.gdogaru.codecamp.view.session.SessionExpandedActivity;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import icepick.State;

public class CalendarFragment extends SessionsFragment implements Injectable {

    private static final Comparator<? super Session> SESSION_BY_DATE_COMPARATOR = (Comparator<Session>) (lhs, rhs) -> lhs.getStartTime().compareTo(rhs.getStartTime());

    ArrayList<Integer> sessIds = new ArrayList<Integer>();
    Timer currentTimer;
    @Inject
    CodecampClient codecampClient;
    @State
    Calendar.CalendarState calendarState;
    private int offset;
    private Calendar calendar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        calendar = new Calendar(getActivity());
        return calendar;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateDisplay();
    }

    @Override
    public void onResume() {
        super.onResume();
        currentTimer = new Timer();
        currentTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                calendar.post(() -> calendar.updateCurrentTime(DateTime.now()));
            }
        }, 500, 30_000);

        calendar.setBookmarked(bookmarkingService.getBookmarked(codecampClient.getEvent().getTitle()));
        if (calendarState != null) {
            calendar.postDelayed(() -> calendar.setState(calendarState), 300);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (currentTimer != null) {
            currentTimer.cancel();
        }
        calendarState = calendar.getState();
    }

    public void updateDisplay() {
        Schedule schedule = codecampClient.getSchedule();
        List<Session> sessions = new ArrayList<>(schedule.getSessions());
        List<Track> tracks = new ArrayList<>(schedule.getTracks());
        Set<String> bookmarked = bookmarkingService.getBookmarked(codecampClient.getEvent().getTitle());

        if (getFavoritesOnly()) {
            keepFavoritesOnly(sessions, tracks, codecampClient.getEvent().getTitle(), bookmarked);
        }

        List<CEvent> events = new ArrayList<>();
        Collections.sort(sessions, SESSION_BY_DATE_COMPARATOR);
        for (int i = 0; i < sessions.size(); i++) {
            Session ss = sessions.get(i);
            int preferedIdx = 0;
            if (ss.getTrack() != null) {
                Track track = getTrack(tracks, ss.getTrack());
                if (track != null) preferedIdx = track.getDisplayOrder();
            }
            String descLine2 = ss.getTrack();
            events.add(new CEvent(ss.getId(), ss.getStartTime(), ss.getEndTime(), preferedIdx, ss.getTitle(),
                    createSpeakerName(ss),
                    descLine2 == null ? "" : descLine2));
        }
        initSessionIds(sessions);
        calendar.setCurrentTime(DateTime.now());
        calendar.setEvents(events);
        calendar.setScheduleDate(schedule.getDate());

        calendar.setEventListener(event -> displayEventDetails(event.event.id));
    }

    private void keepFavoritesOnly(List<Session> sessions, List<Track> tracks, String eventId, Set<String> bookmarked) {
        for (Iterator<Session> iterator = sessions.iterator(); iterator.hasNext(); ) {
            Session s = iterator.next();
            if (!bookmarked.contains(s.getId())) {
                iterator.remove();
            }
        }
        Set<String> rt = new HashSet<>();
        for (Session s : sessions) rt.add(s.getTrack());
        for (Iterator<Track> iterator = tracks.iterator(); iterator.hasNext(); ) {
            Track t = iterator.next();
            if (!rt.contains(t.getName())) iterator.remove();
        }
    }

    private Track getTrack(List<Track> tracks, String track) {
        for (Track t : tracks) {
            if (t.getName().equals(track)) {
                return t;
            }
        }
        return null;
    }


    private void initSessionIds(List<Session> sessions) {
        List<Session> ss = new ArrayList<>(sessions);
        sessIds = new ArrayList<>();
        for (int i = 0; i < ss.size(); i++) {
            sessIds.add(i);
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
}
