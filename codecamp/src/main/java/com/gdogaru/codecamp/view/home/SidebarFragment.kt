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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.EventSummary
import com.gdogaru.codecamp.di.Injectable
import com.gdogaru.codecamp.util.DateUtil
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.util.autoCleared
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

class SidebarFragment : BaseFragment(), Injectable {

    @BindView(R.id.events)
    lateinit var eventsRecycler: RecyclerView
    private var eventsAdapter by autoCleared<EventsAdapter>()
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.main_sidebar, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        eventsRecycler.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        val decor = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        decor.setDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.list_vertical_divider_sidebar)!!)
        eventsRecycler.addItemDecoration(decor)

        eventsAdapter = EventsAdapter(requireActivity()) { eventSummary -> onItemClicked(eventSummary) }
        eventsRecycler.adapter = eventsAdapter

        viewModel.allEvents().observe(this, Observer { list ->
            eventsAdapter.updateItems(list.sortedBy { it.startDate })
        })
    }

    private fun onItemClicked(item: EventSummary) {
        val refId = item.refId
        viewModel.setActiveEvent(refId)
    }

}

class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


class EventHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.title)
    lateinit var eventTitle: TextView
    @BindView(R.id.date)
    lateinit var eventDate: TextView
    @BindView(R.id.city)
    lateinit var city: TextView

    init {
        ButterKnife.bind(this@EventHolder, itemView)
    }
}

class EventsAdapter(context: Context, private val listener: (EventSummary) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    private val eventList = mutableListOf<EventSummary>()

    override fun getItemViewType(position: Int): Int = if (position < eventList.size) 1 else 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (viewType == 1) EventHolder(inflater.inflate(R.layout.main_sidebar_item, parent, false))
            else FooterHolder(inflater.inflate(R.layout.main_sidebar_item_footer, parent, false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventHolder) {
            val summary = eventList[position]
            holder.eventTitle.text = summary.title
            holder.eventDate.text = DateUtil.formatEventPeriod(summary.startDate, summary.endDate)
            holder.city.text = summary.venue?.city
            holder.itemView.setOnClickListener { listener.invoke(summary) }
        }
    }

    override fun getItemCount(): Int = eventList.size + 1
    fun updateItems(list: List<EventSummary>) {
        eventList.clear()
        eventList.addAll(list)
        notifyDataSetChanged()
    }

}
