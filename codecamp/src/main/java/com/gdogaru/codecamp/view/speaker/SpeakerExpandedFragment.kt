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
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.evernote.android.state.State
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
    @State
    lateinit var speakerId: String

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    private var adapter by autoCleared<SpeakerAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainViewModel::class.java)
        speakerId = SpeakerExpandedFragmentArgs.fromBundle(arguments!!).speakerId
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

        val index = speakers.indexOfFirst { input -> input.name == speakerId }
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
