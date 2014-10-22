package com.gdogaru.codecamp.svc.jobs;

import android.content.Context;
import android.util.Log;

import com.gdogaru.codecamp.Logging;
import com.gdogaru.codecamp.model.Codecamp;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Sponsor;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.prefs.UpdatePrefsUtil;
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
    public UpdateDataJob() {
        super(new Params(1000).requireNetwork());
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Log.i(Logging.TAG, "Refreshing data....");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm").create();
        Codecamp codecamp;
        byte code = SUCCESS;
        try {
            codecamp = downloadData(gson);

        } catch (Exception e) {
            Log.e(Logging.TAG, "Error retrieving server data.", e);
            if (UpdatePrefsUtil.getLastUpdated(context) == 0) {
                try {
                    codecamp = loadFromAssets(gson, context);
                    code = LOADED_FROM_ASSETS;
                } catch (Exception e1) {
                    Log.e(Logging.TAG, "Error retrieving data.", e);
                    return ERROR_LOADING;
                }
            } else {
                return ERROR_LOADING;
            }
        }
        SpeakerPhotoUtils.removeAll(context);
        eraseDbData();
        saveDbData(codecamp);
        OverviewDAO.saveOverview(codecamp, gson, context);
        return code;
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }


    private Codecamp downloadData(Gson gson) {
        return codecampClient.getEventData();
    }

    private Codecamp loadFromAssets(Gson gson, Context context) throws IOException {
        InputStream i = context.getAssets().open("codecamp.json");
        String json = IOUtils.toString(i);
        return gson.fromJson(json, Codecamp.class);
    }

    private void eraseDbData() {
        dbHelper.recreateStructure();
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
