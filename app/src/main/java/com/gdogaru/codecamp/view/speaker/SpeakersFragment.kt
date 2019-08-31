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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.api.model.Speaker
import com.gdogaru.codecamp.databinding.SpeakersBinding
import com.gdogaru.codecamp.util.AppExecutors
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.common.GridSpacingItemDecoration
import com.gdogaru.codecamp.view.common.UiUtil
import com.gdogaru.codecamp.view.util.autoCleared
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

class SpeakersFragment : BaseFragment() {
    @Inject
    lateinit var appExecutors: AppExecutors
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel: MainViewModel by viewModels { viewModelFactory }

    private var binding by autoCleared<SpeakersBinding>()
    private var speakersAdapter by autoCleared<SpeakersAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.speakers,
            container,
            false,
            dataBindingComponent
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ma = activity as AppCompatActivity
        ma.setSupportActionBar(binding.toolbar)
        ma.supportActionBar?.apply { setDisplayHomeAsUpEnabled(true) }

        speakersAdapter =
            SpeakersAdapter(dataBindingComponent, appExecutors) { id -> showSpeaker(id) }
        binding.recycler.adapter = speakersAdapter
        binding.recycler.layoutManager = GridLayoutManager(activity, 3)
        binding.recycler.addItemDecoration(GridSpacingItemDecoration(3, UiUtil.dpToPx(5f), true))


        viewModel.currentEvent.observe(this, Observer { initData(it) })
    }

    private fun showSpeaker(s: Speaker) {
// todo add navigation animation from photo        val extras = FragmentNavigatorExtras(avatar to "speakerPhoto")
        findNavController().navigate(SpeakersFragmentDirections.showSpeaker(s.name))
    }

    private fun initData(event: Codecamp) {
        var speakerList = event.speakers.orEmpty()
        speakerList = speakerList.sortedBy { it.displayOrder }
        speakersAdapter.submitList(speakerList)
    }

}
