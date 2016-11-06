package com.gdogaru.codecamp.model;

import org.joda.time.LocalTime;

/**
 * Created by Gabriel on 10/2/2016.
 */

public class TimeSlot {
    LocalTime startTime;
    LocalTime endTime;

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
}
