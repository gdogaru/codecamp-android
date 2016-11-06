package com.gdogaru.codecamp.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Gabriel on 10/2/2016.
 */

public class Schedule {
    Date date;
    List<TimeSlot> timeSlots;
    List<Track> tracks;
    List<Session> sessions;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
}
