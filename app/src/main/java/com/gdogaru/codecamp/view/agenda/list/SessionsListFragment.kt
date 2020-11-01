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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.view.agenda.AbstractSessionsListFragment
import com.gdogaru.codecamp.view.agenda.AgendaFragmentDirections
import com.gdogaru.codecamp.view.util.autoCleared
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

class SessionsListFragment : AbstractSessionsListFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel: SessionsListViewModel by viewModels { viewModelFactory }
    private var sessionsAdapter by autoCleared<SessionsAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.agenda_sessions_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<RecyclerView>(R.id.agendaList)


        sessionsAdapter = SessionsAdapter(requireActivity(), ::onSessionClicked)
        listView.adapter = sessionsAdapter
        listView.addItemDecoration(StickHeaderItemDecoration(sessionsAdapter))

        viewModel.sessionItems().observe(this, Observer { s ->
            s?.let { sessionsAdapter.sessions = it }
        })
    }

    private fun onSessionClicked(item: AgendaListItem.SessionListItem) {
        item.id?.let { findNavController().navigate(AgendaFragmentDirections.showSessionInfo(it)) }
    }

    override fun setFavoritesOnly(favoritesOnly: Boolean) {
        viewModel.setFavoritesOnly(favoritesOnly)
    }
}
