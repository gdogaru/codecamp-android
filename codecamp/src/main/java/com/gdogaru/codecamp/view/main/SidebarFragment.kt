package com.gdogaru.codecamp.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.di.Injectable
import com.gdogaru.codecamp.model.EventSummary
import com.gdogaru.codecamp.svc.CodecampClient
import com.gdogaru.codecamp.util.DateUtil
import com.gdogaru.codecamp.view.BaseFragment
import javax.inject.Inject

/**
 * Created by Gabriel on 2/16/2017.
 */

class SidebarFragment : BaseFragment(), Injectable {

    @BindView(R.id.events)
    lateinit var eventsRecycler: RecyclerView
    @Inject
    lateinit var codecampClient: CodecampClient
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.main_sidebar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        eventsRecycler.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        val decor = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        decor.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.list_vertical_divider_sidebar)!!)
        eventsRecycler.addItemDecoration(decor)

        val events = codecampClient.eventsSummary?.sortedBy { it.startDate }
        eventsAdapter = EventsAdapter(LayoutInflater.from(activity), events!!) { eventSummary -> onItemClicked(eventSummary) }
        eventsRecycler.adapter = eventsAdapter

    }

    private fun onItemClicked(item: EventSummary) {
        val refId = item.refId
        codecampClient.setActiveEvent(refId)
        (activity as MainActivity).initDisplay()
    }

    internal class EventHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.title)
        lateinit var eventTitle: TextView
        @BindView(R.id.date)
        lateinit var eventDate: TextView
        @BindView(R.id.city)
        lateinit var city: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    internal class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class EventsAdapter(private val inflater: LayoutInflater, private val eventList: List<EventSummary>, private val listener: (EventSummary) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int = if (position < eventList.size) 1 else 2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                if (viewType == 1) EventHolder(inflater.inflate(R.layout.main_sidebar_item, parent, false))
                else FooterHolder(inflater.inflate(R.layout.main_sidebar_item_footer, parent, false))


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is EventHolder) {
                val summary = eventList[position]
                holder.eventTitle.text = summary.title
                holder.eventDate.text = DateUtil.formatEventPeriod(summary.startDate, summary.endDate)
                holder.city.text = summary.venue.city
                holder.itemView.setOnClickListener { listener.invoke(summary) }
            }
        }

        override fun getItemCount(): Int = eventList.size + 1

    }
}
