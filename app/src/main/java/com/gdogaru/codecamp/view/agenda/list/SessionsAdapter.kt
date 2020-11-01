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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.util.DateUtil
import java.util.*


class SessionsAdapter(
    context: Context,
    private val onSessionClicked: (AgendaListItem.SessionListItem) -> Unit
) : RecyclerView.Adapter<AgendaViewHolder>(), StickHeaderItemDecoration.StickyHeaderInterface {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var sessions: List<AgendaListItem> = ArrayList()
        set(sessions) {
            field = sessions
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = sessions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder =
        when (viewType) {
            AgendaViewHolder.Header.Type -> AgendaViewHolder.Header.create(inflater, parent)
            AgendaViewHolder.Session.Type -> AgendaViewHolder.Session.create(inflater, parent)
            else -> error("Unkown type $viewType")
        }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var pos = itemPosition
        while (pos > 0) {
            if (sessions.getOrNull(pos) is AgendaListItem.HeaderListItem) {
                return pos
            } else {
                pos--
            }
        }
        return 0
    }

    override fun getHeaderLayout(headerPosition: Int): Int = R.layout.agenda_sessions_list_header

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        val item = sessions[headerPosition] as AgendaListItem.HeaderListItem
        (header as? TextView)?.text = item.text
    }

    override fun getItemViewType(position: Int): Int = when (sessions[position]) {
        is AgendaListItem.HeaderListItem -> AgendaViewHolder.Header.Type
        is AgendaListItem.SessionListItem -> AgendaViewHolder.Session.Type
    }

    override fun isHeader(itemPosition: Int): Boolean =
        sessions[itemPosition] is AgendaListItem.HeaderListItem

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        when (holder) {
            is AgendaViewHolder.Header -> holder.bindData(sessions[position] as AgendaListItem.HeaderListItem)
            is AgendaViewHolder.Session -> holder.bindData(
                sessions[position] as AgendaListItem.SessionListItem,
                onSessionClicked
            )
        }
    }

    override fun getItemId(position: Int) = position.toLong()
}

sealed class AgendaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    class Header(
        view: View
    ) : AgendaViewHolder(view) {


        val text: TextView = view as TextView

        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup): AgendaViewHolder {
                return AgendaViewHolder.Header(
                    inflater.inflate(
                        R.layout.agenda_sessions_list_header,
                        parent,
                        false
                    )
                )
            }

            const val Type = 1
        }

        fun bindData(agendaListItem: AgendaListItem.HeaderListItem) {
            text.text = agendaListItem.text
        }
    }

    class Session(
        view: View
    ) : AgendaViewHolder(view) {
        val title: TextView = view.findViewById(R.id.sessionName)
        val time: TextView = view.findViewById(R.id.sessionTime)
        val place: TextView = view.findViewById(R.id.sessionPlace)
        val speaker: TextView = view.findViewById(R.id.sessionSpeaker)

        fun bindData(
            agendaListItem: AgendaListItem.SessionListItem,
            onSessionClicked: (AgendaListItem.SessionListItem) -> Unit
        ) {
            title.text = agendaListItem.name
            time.text = DateUtil.formatPeriod(agendaListItem.start, agendaListItem.end)
            place.text = agendaListItem.trackName
            speaker.text = agendaListItem.speakerNames?.joinToString(separator = ", ")
            itemView.setOnClickListener { onSessionClicked.invoke(agendaListItem) }

            if (agendaListItem.bookmarked) {
                itemView.setBackgroundResource(R.drawable.list_item_background_favorite)
            } else {
                itemView.setBackgroundResource(R.drawable.list_item_background)
            }
        }

        companion object {
            const val Type = 2

            fun create(inflater: LayoutInflater, parent: ViewGroup): AgendaViewHolder {
                return AgendaViewHolder.Session(
                    inflater.inflate(
                        R.layout.agenda_sessions_list_item,
                        parent,
                        false
                    )
                )
            }
        }
    }
}
