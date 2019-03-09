package com.gdogaru.codecamp.svc.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.svc.AppPreferences;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.svc.events.DataLoadingEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import timber.log.Timber;

/**
 * Created by Gabriel on 10/22/2014.
 */
public class UpdateDataJob extends Job {
    public static final byte LOADED_FROM_ASSETS = 10;
    public static final byte ERROR_LOADING = -1;
    public static final byte SUCCESS = 0;
    public static final String TAG = "UpdateDataJob";

    @Inject
    Gson gson;
    @Inject
    EventBus eventBus;
    @Inject
    CodecampClient codecampClient;
    @Inject
    App app;
    @Inject
    AppPreferences appPreferences;

    @Inject
    public UpdateDataJob(Gson gson, EventBus eventBus, CodecampClient codecampClient, App app, AppPreferences appPreferences) {
        this.gson = gson;
        this.eventBus = eventBus;
        this.codecampClient = codecampClient;
        this.app = app;
        this.appPreferences = appPreferences;
    }

    public static void schedule() {
        new JobRequest.Builder(TAG)
                .setExecutionWindow(1, 600_000)
                .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Timber.i("Refreshing data....");
        try {
            signalStart();

            downloadData();
            signalEnd();
        } catch (Throwable t) {
            Timber.w(t, "Could not get data");
            signalError();
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }



    public void downloadData() throws Exception {
        codecampClient.fetchAllData();
    }

    private void signalStart() {
        eventBus.post(new DataLoadingEvent(0));
        appPreferences.setUpdating(true);
    }

    void signalEnd() {
        appPreferences.setUpdating(false);
        eventBus.post(new DataLoadingEvent(100));
    }

    void signalError() {
        appPreferences.setUpdating(false);
        eventBus.post(new DataLoadingEvent(-1));
    }
}
