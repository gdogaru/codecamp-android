package com.gdogaru.codecamp.view.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
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
import com.gdogaru.codecamp.svc.BookmarkingService
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
 * Created by Gabriel on 10/23/2014.
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
    lateinit var bookmarkingService: BookmarkingService

    @State
    var favoritesOnly = false


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainViewModel::class.java)
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
        if (checked != favoritesOnly) {
            if (checked && bookmarkingService.getBookmarked(event.title).isEmpty()) {
                favoritesOnly = false
                favoriteSwitch.isChecked = false
                Toast.makeText(activity, R.string.no_favorites_yet, Toast.LENGTH_SHORT).show()
                return
            }

            favoritesOnly = checked
            val f = childFragmentManager.findFragmentById(R.id.content) as SessionsFragment?
            f?.setFavoritesOnly(favoritesOnly)
        }
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
