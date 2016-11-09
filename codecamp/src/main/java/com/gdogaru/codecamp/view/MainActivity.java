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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.EventList;
import com.gdogaru.codecamp.model.EventSummary;
import com.gdogaru.codecamp.model.Schedule;
import com.gdogaru.codecamp.svc.AppPreferences;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.svc.jobs.UpdateDataJob;
import com.gdogaru.codecamp.util.RatingHelper;
import com.gdogaru.codecamp.util.Strings;
import com.gdogaru.codecamp.view.agenda.AgendaActivity;
import com.gdogaru.codecamp.view.common.DividerItemDecoration;
import com.gdogaru.codecamp.view.common.RecyclerItemClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.path.android.jobqueue.JobManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;


public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
    @Inject
    CodecampClient codecampClient;
    @Inject
    AppPreferences appPreferences;
    @Inject
    JobManager jobManager;
    @Inject
    FirebaseAnalytics firebaseAnalytics;
    @BindView(R.id.eventTitle)
    TextView eventTitle;
    @BindView(R.id.eventDate)
    TextView eventDate;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.mapLayout)
    LinearLayout mapLayout;
    @BindView(R.id.agenda)
    RecyclerView agenda;
    @BindView(R.id.location_spinner)
    Spinner locationSpinner;
    private SupportMapFragment mapview;
    private SchedulesAdapter schedulesAdapter;
    private GoogleMap googleMap;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        App.getDiComponent().inject(this);
        ButterKnife.bind(this);

        RatingHelper.logUsage(this);

        initSpinner();
        setMap();
        initDisplay();
    }

    private void initDisplay() {
        printData(codecampClient.getEvent());
        displayMap();
    }

    private void initSpinner() {
        EventList el = codecampClient.getEventsSummary();
        List<String> names = Lists.newArrayList(Iterables.transform(el, input -> input.getVenue().getCity()));
        names.add(getString(R.string.refrsh_data));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, names);
        adapter.setDropDownViewResource(R.layout.dropdown_item_drop);
        locationSpinner.setAdapter(adapter);
        int position = Iterables.indexOf(el, input -> input.getRefId() == codecampClient.getEvent().getRefId());
        locationSpinner.setSelection(position);
    }

    private void setMap() {
        if (getSupportFragmentManager().findFragmentById(R.id.content) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mapview = new SupportMapFragment();
            transaction.add(R.id.mapLayout, mapview);
            transaction.commit();
        }

        mapview.getMapAsync(this);
    }

    public void printData(EventSummary overview) {
        Bundle bundle = new Bundle();
        bundle.putString("event", overview.getTitle());
        firebaseAnalytics.logEvent("event_view_"+overview.getTitle(), bundle);

        eventTitle.setText(overview.getTitle());
        eventDate.setText(DATE_FORMAT.format(overview.getStartDate()));
        location.setText(overview.getVenue().getName());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        agenda.setLayoutManager(linearLayoutManager);
        DividerItemDecoration decor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, ContextCompat.getDrawable(this, R.drawable.list_vertical_divider));
        agenda.addItemDecoration(decor);

        schedulesAdapter = new SchedulesAdapter(this, codecampClient.getEvent().getSchedules());
        agenda.setAdapter(schedulesAdapter);
        agenda.setHasFixedSize(true);
        agenda.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Schedule schedule = schedulesAdapter.getItem(position);
                appPreferences.setActiveSchedule(position);
                AgendaActivity.start(MainActivity.this);
            }
        }));
    }

    @OnItemSelected(R.id.location_spinner)
    public void onLocationSelected(Spinner spinner, int position) {
        if (position == spinner.getAdapter().getCount() - 1) {
            LoadingDataActivity.startUpdate(this);
        } else {
            long refId = codecampClient.getEventsSummary().get(position).getRefId();
            if (refId != codecampClient.getEvent().getRefId()) {
                codecampClient.setActiveEvent(refId);
                initDisplay();
            }
        }
    }

    @OnClick(R.id.sponsors)
    public void showSponsors() {
        SponsorsActivity.start(this);
    }

    @OnClick(R.id.speakers)
    public void showSpeakers() {
        SpeakersActivity.start(this);
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
        checkAndRequestPermissions();
    }

    @Override
    public void onPermissionGranted() {
        jobManager.addJob(new UpdateDataJob());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        displayMap();
    }

    private void displayMap() {
        if (googleMap == null) return;
        String d = codecampClient.getEvent().getVenue().getDirections();
        if (Strings.isNullOrEmpty(d)) return;
        String[] dd = d.split(", ");
        final double latitude = Double.parseDouble(dd[0]);
        final double longitude = Double.parseDouble(dd[1]);
        LatLng latLng = new LatLng(latitude, longitude);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //set zoom level
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //add marker to map
        googleMap.addMarker(new MarkerOptions().position(latLng));
        // disables zoom gestures
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        //disable scroll gesture
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //geo:latitude,longitude?z=zoom
                String uri = "geo:" + latitude + "," + longitude + "?z=" + 19;
                Intent intentMap = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intentMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentMap);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        RatingHelper.tryToRate(this);
    }

    static class ScheduleHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;

        public ScheduleHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }

    static class SchedulesAdapter extends RecyclerView.Adapter<ScheduleHolder> {
        private final LayoutInflater layoutInflater;
        DateFormat DATE_FORMAT = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        List<Schedule> schedules;

        public SchedulesAdapter(Context context, List<Schedule> schedules) {
            this.schedules = schedules;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public ScheduleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ScheduleHolder(layoutInflater.inflate(R.layout.main_schedule_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ScheduleHolder holder, int position) {
            Schedule schedule = schedules.get(position);
            holder.title.setText("Event Schedule");
            holder.date.setText(DATE_FORMAT.format(schedule.getDate()));
        }

        @Override
        public int getItemCount() {
            return schedules.size();
        }

        public Schedule getItem(int position) {
            return schedules.get(position);
        }
    }
}
