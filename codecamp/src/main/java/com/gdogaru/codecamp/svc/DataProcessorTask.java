package com.gdogaru.codecamp.svc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gdogaru.codecamp.Logging;
import com.gdogaru.codecamp.db.DatabaseHelper;
import com.gdogaru.codecamp.model.Codecamp;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Sponsor;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.prefs.UpdatePrefsUtil;
import com.gdogaru.codecamp.util.IOUtils;
import com.gdogaru.codecamp.view.MainActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.io.InputStream;

public class DataProcessorTask extends AsyncTask<Void, Byte, Byte> {
    public static final byte LOADED_FROM_ASSETS = 10;
    public static final byte ERROR_LOADING = -1;
    public static final byte SUCCESS = 0;
    CodecampClient codecampClient;
    DatabaseHelper dbHelper;
    private Context context;
    private ProgressDialog progressDialog;

    public DataProcessorTask(Context context, CodecampClient codecampClient) {
        this.context = context;
        this.codecampClient = codecampClient;
    }



    @Override
    protected Byte doInBackground(Void... params) {
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
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "Loading...", "The app is currently refreshing event data...");
        this.dbHelper = OpenHelperManager.getHelper(context.getApplicationContext(), DatabaseHelper.class);
    }

    @Override
    protected void onPostExecute(Byte resultCode) {
        OpenHelperManager.releaseHelper();

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (resultCode == SUCCESS) {
            startMain();
        } else if (resultCode == LOADED_FROM_ASSETS) {
            Toast.makeText(context, "Displaying bundled data. Please update once you have internet connection.", Toast.LENGTH_SHORT).show();
            startMain();
        } else {
            Toast.makeText(context, "Error refreshing data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMain() {
        UpdatePrefsUtil.setLastUpdated(context, System.currentTimeMillis());
        Intent mainActivity = new Intent(context, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(mainActivity);
    }

    @Override
    protected void onProgressUpdate(Byte... values) {
        super.onProgressUpdate(values);
    }


}
