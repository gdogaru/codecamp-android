package com.gdogaru.codecamp.svc.jobs;

/**
 * Created by Gabriel on 10/22/2014.
 */
public class DataLoadingEvent {
    public boolean completed = false;

    public DataLoadingEvent() {
    }

    public DataLoadingEvent(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
