/*
 * Copyright (c) 2019 Gabriel Dogaru - gdogaru@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.gdogaru.codecamp.view.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.crashlytics.android.Crashlytics
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.databinding.HomeBinding
import com.gdogaru.codecamp.util.AnalyticsHelper
import com.gdogaru.codecamp.util.AppExecutors
import com.gdogaru.codecamp.util.RatingHelper
import com.gdogaru.codecamp.view.BaseActivity
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainActivity
import com.gdogaru.codecamp.view.util.autoCleared
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
    @Inject
    lateinit var appExecutors: AppExecutors
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var mapFragment: SupportMapFragment? = null
    private var binding by autoCleared<HomeBinding>()
    lateinit var viewModel: HomeViewModel

    var adapter by autoCleared<BindingScheduleAdapter>()

    private val isNetworkConnected: Boolean
        get() {
            val cm = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = cm.activeNetworkInfo
            return ni != null
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.home,
                container,
                false,
                dataBindingComponent
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)


        val ma = activity as MainActivity
        ma.setSupportActionBar(binding.toolbar)
        ma.supportActionBar!!.title = ""
        ma.supportActionBar!!.setLogo(R.drawable.codecamp_logo)
        ma.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(activity, binding.drawerLayout, binding.toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        binding.drawerLayout.addDrawerListener(toggle)


        val decor = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        decor.setDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.list_vertical_divider)!!)
        binding.agenda.addItemDecoration(decor)

        adapter = BindingScheduleAdapter(dataBindingComponent, appExecutors) { onItemClicked(it) }
        binding.agenda.adapter = adapter

        RatingHelper.logUsage(activity)
        setMap()
        viewModel.currentEvent.observe(this, androidx.lifecycle.Observer { showEvent(it) })

    }

    private fun showEvent(currentEvent: Codecamp) {
        firebaseAnalytics.logEvent(AnalyticsHelper.normalize("event_" + currentEvent.title),
                Bundle().apply { putString("event", currentEvent.title) })

        binding.summary = currentEvent
        adapter.submitList(schedules(currentEvent))

        binding.drawerLayout.closeDrawer(Gravity.LEFT)
    }

    private fun schedules(codecamp: Codecamp): List<MainViewItem> {
        val items = ArrayList<MainViewItem>()
        codecamp.schedules.orEmpty().forEachIndexed { idx, s ->
            items.add(MainViewItem.AgendaItem(getString(R.string.event_schedule), idx, s))
        }
        items.add(MainViewItem.SpeakersItem(getString(R.string.speakers)))
        items.add(MainViewItem.SponsorsItem(getString(R.string.sponsors)))
        return items
    }

    private fun setMap() {
        Timber.i("Adding map")
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }


    private fun onItemClicked(item: MainViewItem) {
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
            (requireActivity() as BaseActivity).checkAndRequestPermissions()
        }
    }

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
                if (d.isNotEmpty()) {
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
        RatingHelper.tryToRate(requireActivity())
    }
}
