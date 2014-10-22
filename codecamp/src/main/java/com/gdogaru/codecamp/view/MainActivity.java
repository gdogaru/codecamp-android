/*
 * Copyright (C) 2008 Gabriel Dogaru (gdogaru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdogaru.codecamp.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gdogaru.codecamp.CodecampApplication;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.svc.DataProcessorTask;
import com.gdogaru.codecamp.view.calendar.CalendarFragment;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.viewpagerindicator.TitlePageIndicator;


public class MainActivity extends Activity {


    CodecampClient codecampClient;

    ViewPager viewPager;
    TitlePageIndicator titles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initViews();
        codecampClient = ((CodecampApplication) getApplication()).getCodecampClient();
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


    public void initViews() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        titles = (TitlePageIndicator) findViewById(R.id.titles);
        viewPager.setAdapter(new TabsAdapter(getFragmentManager(), this));
        titles.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshData() {
        new DataProcessorTask(this, codecampClient).execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    public static class TabsAdapter extends FragmentStatePagerAdapter {

        private final Context context;

        public TabsAdapter(FragmentManager fragmentManager, MainActivity context) {
            super(fragmentManager);
            this.context = context;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem( int position) {
            switch (position) {
                case 0:
                    return new OverviewTabFragment();
                case 1:
                    CalendarFragment calendarFragment = new CalendarFragment();
                    calendarFragment.setOffset(0);
                    return calendarFragment;
                case 2:
                    SessionsTabFragment sessionsTabFragment = new SessionsTabFragment();
//                    sessionsTabFragment_.setRetainInstance(true);
                    return sessionsTabFragment;
//                case 3:
//                    return new AboutTabFragment();
            }
            throw new IllegalStateException();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return context.getString(R.string.overview);
                case 1:
                    return context.getString(R.string.calendar);
                case 2:
                    return context.getString(R.string.sessions);
//                case 3:
//                    return context.getString(R.string.about);
            }
            throw new IllegalStateException();
        }
    }
}
