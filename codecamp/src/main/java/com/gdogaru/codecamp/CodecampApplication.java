package com.gdogaru.codecamp;

import android.app.Application;
import android.util.Log;

import com.gdogaru.codecamp.db.DatabaseHelper;
import com.gdogaru.codecamp.model.json.DateTypeAdapter;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 */
public class CodecampApplication extends Application {

    private static CodecampApplication instance;
    private Tracker appTracker;
    private CodecampClient codecampClient;

    private JobManager jobManager;
    private DatabaseHelper dbHelper;
    EventBus eventBus;
    private Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        configureJobManager();
        dbHelper = OpenHelperManager.getHelper(instance, DatabaseHelper.class);
        eventBus = new EventBus();
        gson = createGson();
        getTracker();
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ")
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
    }

    synchronized public Tracker getTracker() {
        if (appTracker == null) {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
                Log.d("Codecamp", "Google play services available");
            } else {
                Log.w("Codecamp", "Google play services is not available");
            }
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            appTracker = analytics.newTracker(R.xml.app_tracker);
            appTracker.enableAutoActivityTracking(true);
        }
        return appTracker;
    }

    public CodecampClient getCodecampClient() {
        if (codecampClient == null) {
            codecampClient = new CodecampClient();
        }
        return codecampClient;
    }

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minute
                .build();
        jobManager = new JobManager(this, configuration);
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public static CodecampApplication instance() {
        return instance;
    }


    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Gson getGson() {
        return gson;
    }
}
