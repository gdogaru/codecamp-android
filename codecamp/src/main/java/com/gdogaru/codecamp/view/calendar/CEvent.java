package com.gdogaru.codecamp.view.calendar;


import java.util.Date;

public class CEvent {

    public final long id;
    public final Date start;
    public final Date end;
    public final int preferedIdx;
    public final String title;
    public final String descLine1;
    public final String descLine2;

    public CEvent(long id, Date start, Date end, int preferedIdx, String title, String descLine1, String descLine2) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.preferedIdx = preferedIdx;
        this.title = title;
        this.descLine1 = descLine1;
        this.descLine2 = descLine2;
    }


}
