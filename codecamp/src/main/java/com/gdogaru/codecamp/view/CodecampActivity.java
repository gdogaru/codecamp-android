package com.gdogaru.codecamp.view;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.gdogaru.codecamp.CodecampApplication;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.svc.jobs.DataLoadingEvent;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * Created by Gabriel on 10/22/2014.
 */
public class CodecampActivity extends FragmentActivity {
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
    public void setChildActionBar(int textid) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(textid);
            actionBar.setIcon(R.drawable.icon_transparent);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
