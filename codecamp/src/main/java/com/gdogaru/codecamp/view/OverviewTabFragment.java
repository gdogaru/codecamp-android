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


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gdogaru.codecamp.Logging;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Overview;
import com.gdogaru.codecamp.svc.OverviewDAO;
import com.gdogaru.codecamp.util.DateUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class OverviewTabFragment extends Fragment {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMMM yyyy", Locale.US);

    TextView eventTitle;

    TextView eventDate;

    TextView eventLength;

    TextView eventDescription;

    TextView location;

    TextView locationNotes;

    LinearLayout mapLayout;
    private SupportMapFragment mapview;
    private Overview overview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.overview_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventTitle = (TextView) view.findViewById(R.id.eventTitle);
        eventDate = (TextView) view.findViewById(R.id.eventDate);
        eventLength = (TextView) view.findViewById(R.id.eventLength);
        eventDescription = (TextView) view.findViewById(R.id.eventDescription);
        location = (TextView) view.findViewById(R.id.location);
        locationNotes = (TextView) view.findViewById(R.id.locationNotes);
        mapLayout = (LinearLayout) view.findViewById(R.id.mapLayout);
        init();
    }

    public void init() {
        readOverview();

//        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        if (activeNetworkInfo != null) {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            mapLayout.setVisibility(View.VISIBLE);

        }
//        } else {
//            mapLayout.setVisibility(View.GONE);
//        }
        setMap();
    }

    public void readOverview() {
        try {
            overview = OverviewDAO.readOverview(getActivity());
            printData(overview);
        } catch (Exception e) {
            Log.e(Logging.TAG, "Error retrieving overview.", e);
        }
    }

    //    @UiThread
    public void printData(Overview overview) {
        eventTitle.setText(overview.getTitle());
        eventDescription.setText(overview.getDescription());
        String date = DateUtil.formatPeriod(overview.getStartDate(), overview.getEndDate());
        eventLength.setText(date);
        eventDate.setText(DATE_FORMAT.format(overview.getStartDate()));
        location.setText(overview.getLocation().getName());
        locationNotes.setText(overview.getLocation().getNotes());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        //http://code.google.com/p/android/issues/detail?id=19917
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    private void setMap() {
        android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        mapview = new SupportMapFragment();
        transaction.add(R.id.mapLayout, mapview);
        transaction.commit();


//        centerMap();

//
//        try {
//            MapsInitializer.initialize(getActivity());
//        } catch (GooglePlayServicesNotAvailableException impossible) {
//        /* Impossible */
//        }


    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                centerMap();
            }
        }, 500);
    }

    private void centerMap() {
        if (mapview != null && mapview.getMap() != null) {
            final double latitude = overview.getLocation().getLatitude();
            final double longitude = overview.getLocation().getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            GoogleMap googleMap = mapview.getMap();
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
    }
}
