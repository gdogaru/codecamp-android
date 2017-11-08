package com.gdogaru.codecamp.svc.jobs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.svc.AppPreferences;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;

import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Gabriel on 10/22/2014.
 */
public class UpdateDataJob extends Job {
    public static final byte LOADED_FROM_ASSETS = 10;
    public static final byte ERROR_LOADING = -1;
    public static final byte SUCCESS = 0;
    private static final Logger LOG = getLogger(UpdateDataJob.class);

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

    public UpdateDataJob() {
        super(new Params(1000));
    }

    @Override
    public void onAdded() {

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) App.instance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    @Override
    public void onRun() throws Throwable {
        LOG.info("Refreshing data....");
        App.getDiComponent().inject(this);
        signalStart();

        downloadData();
        signalEnd();
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        LOG.info("Canceling job....");
        signalEnd();
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        signalEnd();
        return RetryConstraint.CANCEL;

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

}
