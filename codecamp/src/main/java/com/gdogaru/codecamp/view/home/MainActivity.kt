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

package com.gdogaru.codecamp.view.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.crashlytics.android.Crashlytics
import com.gdogaru.codecamp.App
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.model.EventSummary
import com.gdogaru.codecamp.svc.AppPreferences
import com.gdogaru.codecamp.svc.CodecampClient
import com.gdogaru.codecamp.util.AnalyticsHelper
import com.gdogaru.codecamp.util.RatingHelper
import com.gdogaru.codecamp.util.Strings
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.LoadingDataActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class MainActivity : BaseFragment(), OnMapReadyCallback {
    @Inject
    internal var codecampClient: CodecampClient? = null
    @Inject
    internal var appPreferences: AppPreferences? = null
    @Inject
    internal var firebaseAnalytics: FirebaseAnalytics? = null

    @BindView(R.id.eventTitle)
    internal var eventTitle: TextView? = null
    @BindView(R.id.eventDate)
    internal var eventDate: TextView? = null
    @BindView(R.id.location)
    internal var location: TextView? = null
    @BindView(R.id.agenda)
    internal var agendaRecycler: RecyclerView? = null
    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.drawer_layout)
    internal var drawerLayout: DrawerLayout? = null
    private var mapFragment: SupportMapFragment? = null

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

    private val schedules: List<MainViewItem>
        get() {
            val items = ArrayList<MainViewItem>()
            for (s in codecampClient!!.event!!.schedules) {
                items.add(MainViewItem.AgendaItem(s))
            }
            items.add(MainViewItem.SpeakersItem())
            items.add(MainViewItem.SponsorsItem())
            return items
        }

    private val isNetworkConnected: Boolean
        get() {
            val cm = App.instance()!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = cm.activeNetworkInfo
            return ni != null
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this)
        RatingHelper.logUsage(this)

//        setSupportActionBar(toolbar)
//        supportActionBar!!.setTitle("")
//        supportActionBar!!.setLogo(R.drawable.codecamp_logo)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

//        toolbar!!.setNavigationOnClickListener { v ->
//            if (supportFragmentManager.backStackEntryCount == 0) {
//                drawerLayout!!.openDrawer(Gravity.LEFT)
//            } else {
//                onBackPressed()
//            }
//        }

        drawerLayout!!.addDrawerListener(toggle)

        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        agendaRecycler!!.layoutManager = linearLayoutManager
        val decor = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        decor.setDrawable(ContextCompat.getDrawable(activity, R.drawable.list_vertical_divider)!!)
        agendaRecycler!!.addItemDecoration(decor)

        initSpinner()
        setMap()
        initDisplay()
    }

    fun initDisplay() {
        printData(codecampClient!!.event)
        drawerLayout!!.closeDrawer(Gravity.LEFT)
    }

    private fun initSpinner() {
        val el = codecampClient!!.eventsSummary

        val names = ArrayList<String>()
        if (el != null) {
            for (e in el) {
                if (e.venue != null && e.venue.city != null) {
                    names.add(e.venue.city)
                }
            }
        }
        names.add(getString(R.string.refrsh_data))
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, names)
        adapter.setDropDownViewResource(R.layout.dropdown_item_drop)
        //        locationSpinner.setAdapter(adapter);
        //        int position = Iterables.indexOf(el, input -> input.getRefId() == codecampClient.getEvent().getRefId());
        //        locationSpinner.setSelection(position);
    }

    private fun setMap() {
        Timber.i("Adding map")
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    fun printData(overview: EventSummary?) {
        val bundle = Bundle()
        bundle.putString("event", overview!!.title)
        firebaseAnalytics!!.logEvent(AnalyticsHelper.normalize("event_" + overview.title), bundle)

        eventTitle!!.text = overview.title
        eventDate!!.text = DATE_FORMAT.print(overview.startDate)
        location!!.text = overview.venue.name

        val schedulesAdapter = SchedulesAdapter(this, schedules) { p -> onItemClicked(p.second!!, p.first) }
        agendaRecycler!!.adapter = schedulesAdapter
        agendaRecycler!!.setHasFixedSize(true)
    }

    private fun onItemClicked(position: Int, item: MainViewItem?) {
        if (item is MainViewItem.AgendaItem) {
//            appPreferences!!.activeSchedule = position
//            AgendaActivity.start(this@MainActivity)
        } else if (item is MainViewItem.SpeakersItem) {
//            SpeakersActivity.start(this)
        } else if (item is MainViewItem.SponsorsItem) {
//            SponsorsActivity.start(this)
        } else {
            throw IllegalStateException("Could not show " + item!!)
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.menu, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.refresh -> {
                refreshData()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun refreshData() {
        if (!isNetworkConnected) {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
        } else {
            checkAndRequestPermissions()
        }
    }

    override fun onPermissionGranted() {
        LoadingDataActivity.startUpdate(this)
        //        jobManager.addJob(new UpdateDataJob());
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Timber.i("On map ready %s", googleMap)

        if (googleMap == null) {
            Crashlytics.log("No map loaded")
            Timber.e("No map...")
            return
        }
        try {
            val d = codecampClient!!.event!!.venue.directions
            Timber.i("Centering map to destination %s", d)
            if (Strings.isNullOrEmpty(d)) return
            val dd = d.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val latitude = java.lang.Double.parseDouble(dd[0].trim { it <= ' ' })
            val longitude = java.lang.Double.parseDouble(dd[1].trim { it <= ' ' })
            val latLng = LatLng(latitude, longitude)
            Timber.d("Moving camera to %s", latLng)

            //            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            //add marker to map
            googleMap.addMarker(MarkerOptions().position(latLng))
            // disables zoom gestures
            googleMap.uiSettings.isZoomControlsEnabled = false
            //disable scroll gesture
            googleMap.uiSettings.isScrollGesturesEnabled = false
            googleMap.setOnMapClickListener { latLng1 ->
                //geo:latitude,longitude?z=zoom
                val uri = "geo:$latitude,$longitude?z=19"
                val intentMap = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intentMap.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentMap)
            }
        } catch (e: Exception) {
            Timber.e(e, "Could not parse location")
            Crashlytics.log(Log.ERROR, "Map", "Could not parse location: " + e.message)
        }

    }

    override fun onResume() {
        super.onResume()

//        RatingHelper.tryToRate(this)
    }


    companion object {

        private val DATE_FORMAT = DateTimeFormat.forPattern("dd MMMM yyyy")

        fun start(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(intent)
        }
    }
}
