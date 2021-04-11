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

package com.gdogaru.codecamp.view.sponsors

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.api.model.Sponsor
import com.gdogaru.codecamp.databinding.SponsorsBinding
import com.gdogaru.codecamp.util.AppExecutors
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.common.GridSpacingItemDecoration
import com.gdogaru.codecamp.view.common.UiUtil
import com.gdogaru.codecamp.view.util.autoCleared
import java.util.*
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

class SponsorsFragment : BaseFragment() {

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel: MainViewModel by viewModels { viewModelFactory }
    private var binding by autoCleared<SponsorsBinding>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.sponsors,
            container,
            false,
            dataBindingComponent
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    private var sponsorsAdapter by autoCleared<SponsorsAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val act = activity as AppCompatActivity
        act.setSupportActionBar(binding.toolbar)
        act.supportActionBar?.apply { setDisplayHomeAsUpEnabled(true) }

        sponsorsAdapter = SponsorsAdapter(dataBindingComponent, appExecutors) { showSponsor(it) }
        binding.recycler.adapter = sponsorsAdapter
        binding.recycler.layoutManager = GridLayoutManager(requireActivity(), 3)
        binding.recycler.addItemDecoration(GridSpacingItemDecoration(3, UiUtil.dpToPx(5f), true))

        viewModel.currentEvent.observe(viewLifecycleOwner, { showData(it) })
    }

    private fun showSponsor(s: Sponsor) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(s.websiteUrl)
        requireActivity().startActivity(i)
    }


    private fun showData(c: Codecamp) {
        var sponsorList = c.sponsors
        val packages = c.sponsorshipPackages.orEmpty()
        val ptoIdx = HashMap<String, Int>()
        for (p in packages) {
            ptoIdx[p.name.orEmpty()] = p.displayOrder
        }
        sponsorList = sponsorList.orEmpty()
            .sortedWith(compareBy({ it.sponsorshipPackage }, { it.displayOrder }))
        sponsorsAdapter.submitList(sponsorList.orEmpty())
    }
}
