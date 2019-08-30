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

package com.gdogaru.codecamp.view.agenda.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.view.agenda.AbstractSessionsListFragment
import com.gdogaru.codecamp.view.agenda.AgendaFragmentDirections
import se.emilsjolander.stickylistheaders.StickyListHeadersListView
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

class SessionsListFragment : AbstractSessionsListFragment() {

    lateinit var listView: StickyListHeadersListView
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SessionsListViewModel
    private var sessionsAdapter by autoCleared<SessionsAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SessionsListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.agenda_sessions_list, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(android.R.id.list)

        sessionsAdapter = SessionsAdapter(requireActivity())
        listView.adapter = sessionsAdapter
        listView.setOnItemClickListener { parent, v, position, id -> onListItemClick(parent as ListView, v, position, id) }

        viewModel.sessionItems().observe(this, androidx.lifecycle.Observer { s ->
            s?.let { sessionsAdapter.sessions = it }
        })
    }

    fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val session = sessionsAdapter.getItem(position)
        session.id?.let {
            findNavController().navigate(AgendaFragmentDirections.showSessionInfo(it))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(LIST_STATE, listView.onSaveInstanceState())
    }

    override fun setFavoritesOnly(favoritesOnly: Boolean) {
        viewModel.setFavoritesOnly(favoritesOnly)
    }

    companion object {
        private const val LIST_STATE = "LIST_STATE"
    }
}
