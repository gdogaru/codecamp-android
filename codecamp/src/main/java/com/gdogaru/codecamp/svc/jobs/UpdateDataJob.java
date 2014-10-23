package com.gdogaru.codecamp.svc.jobs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.gdogaru.codecamp.CodecampApplication;
import com.gdogaru.codecamp.Logging;
import com.gdogaru.codecamp.db.DatabaseHelper;
import com.gdogaru.codecamp.model.Codecamp;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Sponsor;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.OverviewDAO;
import com.gdogaru.codecamp.svc.SpeakerPhotoUtils;
import com.gdogaru.codecamp.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Gabriel on 10/22/2014.
 */
public class UpdateDataJob extends Job {
    public static final byte LOADED_FROM_ASSETS = 10;
    public static final byte ERROR_LOADING = -1;
    public static final byte SUCCESS = 0;

    DatabaseHelper dbHelper;

    public UpdateDataJob() {
        super(new Params(1000));
    }

    @Override
    public void onAdded() {

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) CodecampApplication.instance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    @Override
    public void onRun() throws Throwable {
        Log.i(Logging.TAG, "Refreshing data....");
        CodecampApplication app = CodecampApplication.instance();
        signalStart();
        dbHelper = app.getDbHelper();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm").create();

        Codecamp codecamp;
        try {
            codecamp = downloadData(gson);
        } catch (Exception e) {
            Log.e(Logging.TAG, "Error retrieving server data.", e);
//            if (!isNetworkConnected()) {
//                CodecampApplication.instance().getEventBus().post(new NoInternetEvent());
//            }
            if (UpdatePrefHelper.getLastUpdated() == 0) {
                try {
                    codecamp = loadFromAssets(gson, app);
                    Log.e(Logging.TAG, "Loaded from assets.", e);
                } catch (Exception e1) {
                    Log.e(Logging.TAG, "Error retrieving data.", e);
                    throw new ErrorLoadingException();
                }
            } else {
                throw new ErrorLoadingException();
            }
        }
        SpeakerPhotoUtils.removeAll(app);
        eraseDbData();
        correctData(codecamp);
        saveDbData(codecamp);
        OverviewDAO.saveOverview(codecamp, gson, app);
        UpdatePrefHelper.setLastUpdated(System.currentTimeMillis());
        signalEnd();
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
        CodecampApplication.instance().getEventBus().post(new DataLoadingEvent(false));
        UpdatePrefHelper.setUpdating(true);
    }

    void signalEnd() {
        UpdatePrefHelper.setUpdating(false);
        CodecampApplication.instance().getEventBus().post(new DataLoadingEvent(true));
    }

    private Codecamp downloadData(Gson gson) {
        return CodecampApplication.instance().getCodecampClient().getEventData();
    }

    private Codecamp loadFromAssets(Gson gson, Context context) throws IOException {
        InputStream i = context.getAssets().open("codecamp.json");
        String json = IOUtils.toString(i);
        return gson.fromJson(json, Codecamp.class);
    }

    private void eraseDbData() {
        dbHelper.recreateStructure();
    }

    private void correctData(Codecamp codecamp) {
        for (Session session : codecamp.getSessions()) {
            try {
                session.setDescription(session.getDescription().replaceAll("<br/>", "\n"));
            } catch (Exception e) {
                Log.e(Logging.TAG, "Error parsing data", e);
            }
        }

        for (Speaker speaker : codecamp.getSpeakers()) {
            speaker.setBio(speaker.getBio().replaceAll("<br/>", "\n"));
        }
    }

    private void saveDbData(Codecamp codecamp) {
        for (Session session : codecamp.getSessions()) {
            try {
                dbHelper.getSessionDao().create(session);
            } catch (Exception e) {
                int i = 1;
            }
        }

        for (Speaker speaker : codecamp.getSpeakers()) {
            dbHelper.getSpeakerDao().createOrUpdate(speaker);
        }
        if (codecamp.getSponsors() != null) {
            for (Sponsor sponsor : codecamp.getSponsors()) {
                dbHelper.getSponsorDao().create(sponsor);
            }
        }
        for (Track track : codecamp.getTracks()) {
            dbHelper.getTrackDao().create(track);
        }
    }
}
