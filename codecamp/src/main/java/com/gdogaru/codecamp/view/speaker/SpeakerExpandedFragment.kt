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

package com.gdogaru.codecamp.view.speaker


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
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
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.api.model.Speaker
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainActivity
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.util.autoCleared
import javax.inject.Inject

class SpeakerExpandedFragment : BaseFragment() {

    @BindView(R.id.viewPager)
    lateinit var viewPager: ViewPager
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val args: SpeakerExpandedFragmentArgs by navArgs()

    private lateinit var viewModel: MainViewModel

    private var adapter by autoCleared<SpeakerAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.speaker_expanded, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manage(ButterKnife.bind(this, view))

        val ma = activity as MainActivity?
        ma!!.setSupportActionBar(toolbar)
        toolbar.title = ""
        ma.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        adapter = SpeakerAdapter(childFragmentManager)
        viewPager.adapter = adapter

        viewModel.currentEvent.observe(this, Observer { initViews(it) })
    }


    fun initViews(event: Codecamp) {
        val speakers = event.speakers.orEmpty()
        adapter.updateItems(speakers)

        val index = speakers.indexOfFirst { input -> input.name == args.speakerId }
        viewPager.currentItem = if (index < 0) 0 else index
    }

    private fun restoreState(savedInstanceState: Bundle) {
        //        if (savedInstanceState != null) {
        //            if (savedInstanceState.containsKey(SPEAKER_ID)) {
        //                speakerId = savedInstanceState.getString(SPEAKER_ID);
        //            }
        //        } else if (getIntent() != null && getIntent().hasExtra(SPEAKER_ID)) {
        //            speakerId = getIntent().getStringExtra(SPEAKER_ID);
        //        } else {
        //            Timber.e("Could not show speaker");
        //            finish();
        //        }
    }


    private inner class SpeakerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private var speakers = mutableListOf<Speaker>()

        fun updateItems(value: List<Speaker>) {
            speakers.clear()
            speakers.addAll(value)
            notifyDataSetChanged()
        }

        override fun getItem(position: Int): Fragment {
            return SpeakerInfoFragment.newInstance(speakers[position].name)
        }

        override fun getCount() = speakers.size

        override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE
    }
}
