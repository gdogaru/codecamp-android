package com.gdogaru.codecamp.view.agenda.calendar;


import org.joda.time.LocalTime;

public class CEvent {

    public final String id;
    public final LocalTime start;
    public final LocalTime end;
    public final int preferedIdx;
    public final String title;
    public final String descLine1;
    public final String descLine2;

    public CEvent(String id, LocalTime start, LocalTime end, int preferedIdx, String title, String descLine1, String descLine2) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.preferedIdx = preferedIdx;
        this.title = title;
        this.descLine1 = descLine1;
        this.descLine2 = descLine2;
    }


}
