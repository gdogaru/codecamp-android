package com.gdogaru.codecamp.svc.jobs;

/**
 * Created by Gabriel on 10/22/2014.
 */
public class DataLoadingEvent {
    public int progress = 0;

    public DataLoadingEvent(int progress) {
        this.progress = progress;
    }
}
