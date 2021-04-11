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

package com.gdogaru.codecamp.view.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.evernote.android.state.State
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.databinding.AgendaBinding
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.BookmarkRepository
import com.gdogaru.codecamp.util.AnalyticsHelper
import com.gdogaru.codecamp.util.DateUtil
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.agenda.calendar.CalendarFragment
import com.gdogaru.codecamp.view.agenda.list.SessionsListFragment
import com.gdogaru.codecamp.view.util.autoCleared
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
class AgendaFragment : BaseFragment() {
    private var binding by autoCleared<AgendaBinding>()

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var bookmarkingService: BookmarkRepository

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @State
    var favoritesOnly = false
    private val viewModel: AgendaViewModel by viewModels { viewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.agenda,
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

        binding.viewSwitch.isChecked = appPreferences.listViewList
        binding.viewSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (appPreferences.listViewList != isChecked) {
                appPreferences.listViewList = isChecked
                showContent()
            }
        }

        binding.favoriteSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked != favoritesOnly) {
                favoritesOnly = checked
                val f =
                    childFragmentManager.findFragmentById(R.id.content) as AbstractSessionsListFragment?
                f?.setFavoritesOnly(favoritesOnly)
            }
        }

        val fragmentById =
            childFragmentManager.findFragmentById(R.id.content) as AbstractSessionsListFragment?
        if (fragmentById == null) {
            showContent()
        } else {
            fragmentById.setFavoritesOnly(favoritesOnly)
        }

        viewModel.getSchedule().observe(viewLifecycleOwner, { schedule ->
            schedule?.let { binding.title.text = String.format(DateUtil.formatDay(it.date)) }
        })
    }

    private fun showContent() {
        val bundle = Bundle()
        val value = if (appPreferences.listViewList) "list" else "calendar"
        bundle.putString("view_type", value)
        firebaseAnalytics.logEvent(AnalyticsHelper.normalize("agenda_view_$value"), bundle)

        val transaction = childFragmentManager
            .beginTransaction()
            .disallowAddToBackStack()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out) //, 0, R.anim.hold);
        val sessionsFragment: AbstractSessionsListFragment
        if (appPreferences.listViewList) {
            sessionsFragment = SessionsListFragment()
        } else {
            sessionsFragment = CalendarFragment()
        }
        transaction.replace(R.id.content, sessionsFragment, sessionsFragment.javaClass.name)
        transaction.commit()
    }

}
