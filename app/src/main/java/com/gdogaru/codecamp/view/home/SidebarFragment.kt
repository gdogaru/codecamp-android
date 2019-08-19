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

package com.gdogaru.codecamp.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.android.example.github.ui.common.DataBoundListAdapter
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.EventSummary
import com.gdogaru.codecamp.databinding.HomeSidebarBinding
import com.gdogaru.codecamp.databinding.HomeSidebarItemBinding
import com.gdogaru.codecamp.di.Injectable
import com.gdogaru.codecamp.util.AppExecutors
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.util.autoCleared
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

class SidebarFragment : BaseFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors
    private lateinit var viewModel: SidebarViewModel
    private var eventsAdapter by autoCleared<EventsAdapter>()
    private lateinit var binding: HomeSidebarBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.home_sidebar,
                container,
                false,
                dataBindingComponent
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SidebarViewModel::class.java)

        val decor = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireActivity(), R.drawable.list_vertical_divider_sidebar)?.let { decor.setDrawable(it) }
        binding.events.addItemDecoration(decor)

        eventsAdapter = EventsAdapter(dataBindingComponent, appExecutors) { eventSummary -> onItemClicked(eventSummary) }
        binding.events.adapter = eventsAdapter

        viewModel.allEvents().observe(this, Observer { list ->
            eventsAdapter.submitList(list.sortedBy { it.startDate })
        })
    }

    private fun onItemClicked(item: EventSummary) {
        val refId = item.refId
        viewModel.setActiveEvent(refId)
    }

}

class EventsAdapter(private val dataBindingComponent: DataBindingComponent,
                    appExecutors: AppExecutors,
                    private val listener: (EventSummary) -> Unit)
    : DataBoundListAdapter<EventSummary, HomeSidebarItemBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<EventSummary>() {
            override fun areItemsTheSame(oldItem: EventSummary, newItem: EventSummary): Boolean {
                return oldItem.refId == newItem.refId
            }

            override fun areContentsTheSame(oldItem: EventSummary, newItem: EventSummary): Boolean {
                return oldItem.title == newItem.title
            }
        }
) {

    override fun createBinding(parent: ViewGroup): HomeSidebarItemBinding {
        val binding = DataBindingUtil.inflate<HomeSidebarItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.home_sidebar_item,
                parent,
                false,
                dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.summary?.let {
                listener.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: HomeSidebarItemBinding, item: EventSummary) {
        binding.summary = item
    }
}
