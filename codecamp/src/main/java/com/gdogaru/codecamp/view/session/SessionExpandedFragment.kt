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

package com.gdogaru.codecamp.view.session


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Schedule
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainActivity
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.util.autoCleared
import javax.inject.Inject

class SessionExpandedFragment : BaseFragment(), ViewPager.OnPageChangeListener {
    @BindView(R.id.viewPager)
    lateinit var viewPager: ViewPager
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.bookmarked)
    lateinit var bookmarked: CheckBox

    private val args: SessionExpandedFragmentArgs by navArgs()
    private var adapter by autoCleared<ExpandedSessionsAdapter>()


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.session_expanded_activity, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        val ma = activity as MainActivity?
        ma!!.setSupportActionBar(toolbar)
        ma.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        adapter = ExpandedSessionsAdapter(childFragmentManager)
        viewPager.addOnPageChangeListener(this)
        viewPager.adapter = adapter

        viewModel.currentSchedule().observe(this, androidx.lifecycle.Observer { s ->
            s?.let { initPager(it) }
        })
    }


    private fun initPager(schedule: Schedule) {
        val trackSessions = schedule.sessions.map { it.id }

        var index = -1
        for (i in trackSessions.indices) {
            val t = trackSessions[i]
            if (t == args.sessionId) {
                index = i
                break
            }
        }

        adapter.updateSessionIds(trackSessions)
        viewPager.currentItem = if (index < 0) 0 else index

    }

    //    private void initSelector() {
    //        trackList = codecampClient.getSchedule().getTracks();
    //        Track track = new Track();
    //        track.setName(allTracksString);
    //        trackList.add(0, track);
    //        List<String> trackNames = Lists.newArrayList(Iterables.transform(trackList, Track::getName));
    //        int position = trackNames.indexOf(trackId);
    //        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, trackNames);
    //        adapter.setDropDownViewResource(R.layout.dropdown_item_drop);
    //        trackSpinner.setAdapter(adapter);
    //        trackSpinner.setSelection(position);
    //    }

    //    @OnItemSelected(R.id.track_spinner)
    //    public void onTrackSelected(Spinner spinner, int position) {
    //        String newTrackId = trackList.get(position).getName();
    //
    //        if (!Strings.nullToEmpty(newTrackId).equals(trackId)) {
    //            trackId = newTrackId;
    //            initPager();
    //        }
    //    }

    @OnCheckedChanged(R.id.bookmarked)
    fun onBookmarkChanged(checked: Boolean) {
        viewModel.setBookmarked(adapter.getElement(viewPager.currentItem), checked)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        val s = adapter.getElement(position)
        viewModel.isBookmarked(s).observe(this, Observer {
            bookmarked.isChecked = it
        })
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    private inner class ExpandedSessionsAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        private val trackSessions = mutableListOf<String>()

        fun updateSessionIds(value: List<String>) {
            trackSessions.clear()
            trackSessions.addAll(value)
            notifyDataSetChanged()
        }

        override fun getItem(position: Int) = SessionInfoFragment.newInstance(trackSessions[position])

        override fun getCount() = trackSessions.size

        fun getElement(idx: Int) = trackSessions[idx]

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }

        override fun getItemPosition(o: Any) = PagerAdapter.POSITION_NONE
    }

    companion object {

        val VIEWPAGER = "viewpager"
        private val TRACK_ID = "trackID"
        private val SESSION_ID = "sessionId"
    }
}
