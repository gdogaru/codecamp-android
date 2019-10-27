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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Schedule
import com.gdogaru.codecamp.databinding.SessionExpandedActivityBinding
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.util.autoCleared
import javax.inject.Inject

class SessionExpandedFragment : BaseFragment(), ViewPager.OnPageChangeListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val args: SessionExpandedFragmentArgs by navArgs()
    var binding by autoCleared<SessionExpandedActivityBinding>()
    val viewModel: SessionExpandedViewModel by viewModels { viewModelFactory }
    private var adapter by autoCleared<ExpandedSessionsAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.session_expanded_activity,
            container,
            false,
            dataBindingComponent
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ma = requireActivity() as AppCompatActivity
        ma.setSupportActionBar(binding.toolbar)
        ma.supportActionBar?.apply { setDisplayHomeAsUpEnabled(true) }

        adapter = ExpandedSessionsAdapter(childFragmentManager)
        binding.viewPager.addOnPageChangeListener(this)
        binding.viewPager.adapter = adapter

        viewModel.currentSchedule().observe(this, Observer { s ->
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
        binding.viewPager.currentItem = if (index < 0) 0 else index

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    private inner class ExpandedSessionsAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val trackSessions = mutableListOf<String>()

        fun updateSessionIds(value: List<String>) {
            trackSessions.clear()
            trackSessions.addAll(value)
            notifyDataSetChanged()
        }

        override fun getItem(position: Int) =
            SessionInfoFragment.newInstance(trackSessions[position])

        override fun getCount() = trackSessions.size

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }

        override fun getItemPosition(o: Any) = PagerAdapter.POSITION_NONE
    }

}
