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
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import com.evernote.android.state.State
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.BookmarkRepository
import com.gdogaru.codecamp.util.AnalyticsHelper
import com.gdogaru.codecamp.util.DateUtil
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainActivity
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.agenda.calendar.CalendarFragment
import com.gdogaru.codecamp.view.agenda.list.SessionsListFragment
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
class AgendaFragment : BaseFragment() {
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.view_switch)
    lateinit var viewSwitch: CheckBox
    @BindView(R.id.favorite_switch)
    lateinit var favoriteSwitch: CheckBox
    @BindView(R.id.title)
    lateinit var titleView: TextView

    @Inject
    lateinit var appPreferences: AppPreferences
    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics
    @Inject
    lateinit var bookmarkingService: BookmarkRepository

    @State
    var favoritesOnly = false


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.agenda_activity, container, false)

    private lateinit var event: Codecamp

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ButterKnife.bind(this, view)

        val ma = activity as MainActivity?
        ma!!.setSupportActionBar(toolbar)
        ma.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        viewSwitch.isChecked = appPreferences.listViewList

        favoriteSwitch.isChecked = favoritesOnly


        val fragmentById = childFragmentManager.findFragmentById(R.id.content) as SessionsFragment?
        if (fragmentById == null) {
            showList()
        } else {
            fragmentById.setFavoritesOnly(favoritesOnly)
        }

        viewModel.currentEvent.observe(this, Observer {
            val schedule = it.schedules!![0]
            event = it
            titleView.text = String.format("%s - %s", DateUtil.formatDay(schedule.date), it.venue!!.city)
        })
    }

    @OnCheckedChanged(R.id.view_switch)
    fun onViewTypeChange(checked: Boolean) {
        if (appPreferences.listViewList != viewSwitch.isChecked) {
            appPreferences.listViewList = viewSwitch.isChecked
            showList()
        }
    }

    @OnCheckedChanged(R.id.favorite_switch)
    fun onFavoriteChecked(checked: Boolean) {
//        if (checked != favoritesOnly) {
//            if (checked && bookmarkingService.getBookmarked(event.title.orEmpty())) {
//                favoritesOnly = false
//                favoriteSwitch.isChecked = false
//                Toast.makeText(activity, R.string.no_favorites_yet, Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            favoritesOnly = checked
//            val f = childFragmentManager.findFragmentById(R.id.content) as SessionsFragment?
//            f?.setFavoritesOnly(favoritesOnly)
//        }
    }

    private fun showList() {
        val bundle = Bundle()
        val value = if (appPreferences.listViewList) "list" else "calendar"
        bundle.putString("view_type", value)
        firebaseAnalytics.logEvent(AnalyticsHelper.normalize("agenda_view_$value"), bundle)

        val transaction = childFragmentManager
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out) //, 0, R.anim.hold);
        val sessionsFragment: SessionsFragment
        if (appPreferences.listViewList) {
            sessionsFragment = SessionsListFragment()
        } else {
            sessionsFragment = CalendarFragment()
        }
        transaction.replace(R.id.content, sessionsFragment, sessionsFragment.javaClass.name)
        transaction.commit()
    }

    companion object {

        val FAVORITES_ONLY = "FAVORITES_ONLY"
    }
}
