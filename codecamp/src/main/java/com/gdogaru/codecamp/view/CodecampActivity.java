package com.gdogaru.codecamp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.gdogaru.codecamp.CodecampApplication;
import com.gdogaru.codecamp.svc.jobs.DataLoadingEvent;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * Created by Gabriel on 10/22/2014.
 */
public class CodecampActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CodecampApplication.instance().getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CodecampApplication.instance().getEventBus().unregister(this);
    }

    public void onEvent(DataLoadingEvent event) {
        Class clazz = event.completed ? MainActivity.class : LoadingDataActivity.class;
        Intent intent = new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

//    public void onEventMainThread(NoInternetEvent event) {
//        Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
//    }
}
