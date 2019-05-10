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

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.crashlytics.android.Crashlytics
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.util.AnalyticsHelper
import com.gdogaru.codecamp.util.RatingHelper
import com.gdogaru.codecamp.util.Strings
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainActivity
import com.gdogaru.codecamp.view.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class HomeFragment : BaseFragment(), OnMapReadyCallback {
    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    @BindView(R.id.eventTitle)
    lateinit var eventTitle: TextView
    @BindView(R.id.eventDate)
    lateinit var eventDate: TextView
    @BindView(R.id.location)
    lateinit var location: TextView
    @BindView(R.id.agenda)
    lateinit var agendaRecycler: RecyclerView
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout

    private var mapFragment: SupportMapFragment? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainViewModel::class.java)
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


    private val isNetworkConnected: Boolean
        get() {
            val cm = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = cm.activeNetworkInfo
            return ni != null
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        RatingHelper.logUsage(activity)

        val ma = activity as MainActivity
        ma.setSupportActionBar(toolbar)
        ma.supportActionBar!!.title = ""
        ma.supportActionBar!!.setLogo(R.drawable.codecamp_logo)
        ma.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        drawerLayout.addDrawerListener(toggle)

        agendaRecycler.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        val decor = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        decor.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.list_vertical_divider)!!)
        agendaRecycler.addItemDecoration(decor)

        setMap()

        viewModel.currentEvent.observe(this, androidx.lifecycle.Observer { showEvent(it) })
        setHasOptionsMenu(true)
    }

    private fun showEvent(overview: Codecamp) {
        val bundle = Bundle()
        bundle.putString("event", overview.title)
        firebaseAnalytics.logEvent(AnalyticsHelper.normalize("event_" + overview.title), bundle)

        eventTitle.text = overview.title
        eventDate.text = DATE_FORMAT.format(overview.startDate)
        location.text = overview.venue?.name

        val schedulesAdapter = SchedulesAdapter(activity, schedules(overview)) { p -> onItemClicked(p.first) }
        agendaRecycler.adapter = schedulesAdapter
        agendaRecycler.setHasFixedSize(true)


        drawerLayout.closeDrawer(Gravity.LEFT)
    }

    private fun schedules(codecamp: Codecamp): List<MainViewItem> {
        val items = ArrayList<MainViewItem>()
        codecamp.schedules.orEmpty().forEachIndexed { idx, s ->
            items.add(MainViewItem.AgendaItem(idx, s))
        }
        items.add(MainViewItem.SpeakersItem())
        items.add(MainViewItem.SponsorsItem())
        return items
    }

    private fun setMap() {
        Timber.i("Adding map")
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }


    private fun onItemClicked(item: MainViewItem?) {
        when (item) {
            is MainViewItem.AgendaItem -> {
                viewModel.selectSchedule(item.index)
                findNavController().navigate(HomeFragmentDirections.showAgenda())
            }
            is MainViewItem.SpeakersItem -> findNavController().navigate(HomeFragmentDirections.showSpeakers())
            is MainViewItem.SponsorsItem -> findNavController().navigate(HomeFragmentDirections.showSponsors())
            else -> throw IllegalStateException("Could not show $item")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                refreshData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshData() {
        if (!isNetworkConnected) {
            Toast.makeText(activity, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
        } else {
//            checkAndRequestPermissions()
        }
    }

//    override fun onPermissionGranted() {
//        LoadingDataActivity.startUpdate(activity)
//        //        jobManager.addJob(new UpdateDataJob());
//    }


    override fun onMapReady(googleMap: GoogleMap?) {
        Timber.i("On map ready %s", googleMap)

        if (googleMap == null) {
            Crashlytics.log("No map loaded")
            Timber.e("No map...")
            return
        }
        viewModel.currentEvent.observe(this@HomeFragment, androidx.lifecycle.Observer<Codecamp> { c ->
            try {
                val d = c.venue!!.directions
                Timber.i("Centering map to destination %s", d)
                if (Strings.isNullOrEmpty(d).not()) {
                    val dd = d.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val latitude = java.lang.Double.parseDouble(dd[0].trim { it <= ' ' })
                    val longitude = java.lang.Double.parseDouble(dd[1].trim { it <= ' ' })
                    val latLng = LatLng(latitude, longitude)
                    Timber.d("Moving camera to %s", latLng)
                    //            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    googleMap.addMarker(MarkerOptions().position(latLng))
                    googleMap.uiSettings.isZoomControlsEnabled = false
                    googleMap.uiSettings.isScrollGesturesEnabled = false
                    googleMap.setOnMapClickListener {
                        //geo:latitude,longitude?z=zoom
                        val uri = "geo:$latitude,$longitude?z=19"
                        val intentMap = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        intentMap.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intentMap)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Could not parse location")
                Crashlytics.log(Log.ERROR, "Map", "Could not parse location: " + e.message)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        RatingHelper.tryToRate(activity!!)
    }


    companion object {
        private val DATE_FORMAT = org.threeten.bp.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")
    }
}
