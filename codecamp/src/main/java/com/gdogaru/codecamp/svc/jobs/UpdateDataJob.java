package com.gdogaru.codecamp.svc.jobs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.svc.AppPreferences;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.google.gson.Gson;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

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
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    @Override
    public void onRun() throws Throwable {
        LOG.info("Refreshing data....");
        App.getDiComponent().inject(this);
        signalStart();

        downloadData();
        signalEnd();
    }


    public void downloadData() throws Exception {
        codecampClient.fetchAllData();
    }

    @Override
    protected void onCancel() {
        signalEnd();
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        signalEnd();
        return false;
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
