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

package com.gdogaru.codecamp.view.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.EventList;
import com.gdogaru.codecamp.model.EventSummary;
import com.gdogaru.codecamp.model.Schedule;
import com.gdogaru.codecamp.svc.AppPreferences;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.AnalyticsHelper;
import com.gdogaru.codecamp.util.RatingHelper;
import com.gdogaru.codecamp.util.Strings;
import com.gdogaru.codecamp.view.BaseActivity;
import com.gdogaru.codecamp.view.LoadingDataActivity;
import com.gdogaru.codecamp.view.SponsorsActivity;
import com.gdogaru.codecamp.view.agenda.AgendaActivity;
import com.gdogaru.codecamp.view.speaker.SpeakersActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;


public class MainActivity extends BaseActivity implements OnMapReadyCallback, HasSupportFragmentInjector {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("dd MMMM yyyy");
    @Inject
    CodecampClient codecampClient;
    @Inject
    AppPreferences appPreferences;
    @Inject
    FirebaseAnalytics firebaseAnalytics;
    @BindView(R.id.eventTitle)
    TextView eventTitle;
    @BindView(R.id.eventDate)
    TextView eventDate;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.agenda)
    RecyclerView agendaRecycler;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    private SupportMapFragment mapFragment;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ButterKnife.bind(this);
        RatingHelper.logUsage(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setLogo(R.drawable.codecamp_logo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        toolbar.setNavigationOnClickListener(v -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                drawerLayout.openDrawer(Gravity.LEFT);
            } else {
                onBackPressed();
            }
        });

        drawerLayout.addDrawerListener(toggle);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        agendaRecycler.setLayoutManager(linearLayoutManager);
        DividerItemDecoration decor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decor.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.list_vertical_divider)));
        agendaRecycler.addItemDecoration(decor);

        initSpinner();
        setMap();
        initDisplay();
    }

    public void initDisplay() {
        printData(codecampClient.getEvent());
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void initSpinner() {
        EventList el = codecampClient.getEventsSummary();

        List<String> names = new ArrayList<>();
        if (el != null) {
            for (EventSummary e : el) {
                if (e.getVenue() != null && e.getVenue().getCity() != null) {
                    names.add(e.getVenue().getCity());
                }
            }
        }
        names.add(getString(R.string.refrsh_data));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, names);
        adapter.setDropDownViewResource(R.layout.dropdown_item_drop);
//        locationSpinner.setAdapter(adapter);
//        int position = Iterables.indexOf(el, input -> input.getRefId() == codecampClient.getEvent().getRefId());
//        locationSpinner.setSelection(position);
    }

    private void setMap() {
        Timber.i("Adding map");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void printData(EventSummary overview) {
        Bundle bundle = new Bundle();
        bundle.putString("event", overview.getTitle());
        firebaseAnalytics.logEvent(AnalyticsHelper.normalize("event_" + overview.getTitle()), bundle);

        eventTitle.setText(overview.getTitle());
        eventDate.setText(DATE_FORMAT.print(overview.getStartDate()));
        location.setText(overview.getVenue().getName());

        SchedulesAdapter schedulesAdapter = new SchedulesAdapter(this, getSchedules(), p -> {
            onItemClicked(p.second, p.first);
        });
        agendaRecycler.setAdapter(schedulesAdapter);
        agendaRecycler.setHasFixedSize(true);
    }

//    @OnItemSelected(R.id.location_spinner)
//    public void onLocationSelected(Spinner spinner, int position) {
//        if (position == spinner.getAdapter().getCount() - 1) {
//            LoadingDataActivity.startUpdate(this);
//        } else {
//            long refId = codecampClient.getEventsSummary().get(position).getRefId();
//            if (refId != codecampClient.getEvent().getRefId()) {
//                codecampClient.setActiveEvent(refId);
//                initDisplay();
//            }
//        }
//    }

    private List<MainViewItem> getSchedules() {
        ArrayList<MainViewItem> items = new ArrayList<>();
        for (Schedule s : codecampClient.getEvent().getSchedules()) {
            items.add(new MainViewItem.AgendaItem(s));
        }
        items.add(new MainViewItem.SpeakersItem());
        items.add(new MainViewItem.SponsorsItem());
        return items;
    }

    private void onItemClicked(int position, MainViewItem item) {
        if (item instanceof MainViewItem.AgendaItem) {
            appPreferences.setActiveSchedule(position);
            AgendaActivity.start(MainActivity.this);
        } else if (item instanceof MainViewItem.SpeakersItem) {
            SpeakersActivity.start(this);
        } else if (item instanceof MainViewItem.SponsorsItem) {
            SponsorsActivity.start(this);
        } else {
            throw new IllegalStateException("Could not show " + item);
        }
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
        if (!isNetworkConnected()) {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        } else {
            checkAndRequestPermissions();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) App.Companion.instance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    @Override
    public void onPermissionGranted() {
        LoadingDataActivity.startUpdate(this);
//        jobManager.addJob(new UpdateDataJob());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Timber.i("On map ready %s", googleMap);

        if (googleMap == null) {
            Crashlytics.log("No map loaded");
            Timber.e("No map...");
            return;
        }
        try {
            String d = codecampClient.getEvent().getVenue().getDirections();
            Timber.i("Centering map to destination %s", d);
            if (Strings.isNullOrEmpty(d)) return;
            String[] dd = d.split(",");
            final double latitude = Double.parseDouble(dd[0].trim());
            final double longitude = Double.parseDouble(dd[1].trim());
            LatLng latLng = new LatLng(latitude, longitude);
            Timber.d("Moving camera to %s", latLng);

//            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            //add marker to map
            googleMap.addMarker(new MarkerOptions().position(latLng));
            // disables zoom gestures
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            //disable scroll gesture
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.setOnMapClickListener(latLng1 -> {
                //geo:latitude,longitude?z=zoom
                String uri = "geo:" + latitude + "," + longitude + "?z=" + 19;
                Intent intentMap = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intentMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentMap);
            });
        } catch (Exception e) {
            Timber.e(e, "Could not parse location");
            Crashlytics.log(Log.ERROR, "Map", "Could not parse location: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RatingHelper.tryToRate(this);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
