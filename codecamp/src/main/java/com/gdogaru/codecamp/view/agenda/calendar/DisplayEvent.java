package com.gdogaru.codecamp.view.agenda.calendar;


public class DisplayEvent {

    public final CEvent event;
    public int index = 0;
    public int rowTotal = 0;

    public DisplayEvent(CEvent event) {
        this.event = event;
    }
}
