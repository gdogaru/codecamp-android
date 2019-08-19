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
import android.widget.BaseAdapter
import android.widget.TextView
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.util.DateUtil
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter
import java.util.*


class SessionsAdapter(context: Context) : BaseAdapter(), StickyListHeadersAdapter {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var sessions: List<SessionListItem> = ArrayList()
        set(sessions) {
            field = sessions
            notifyDataSetChanged()
        }

    override fun getCount() = sessions.size

    override fun getItem(position: Int) = sessions[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView != null)
            view = convertView
        else {
            view = inflater.inflate(R.layout.agenda_sessions_list_item, parent, false)
            val holder = ViewHolder(
                    view.findViewById<View>(R.id.sessionName) as TextView,
                    view.findViewById<View>(R.id.sessionTime) as TextView,
                    view.findViewById<View>(R.id.sessionPlace) as TextView,
                    view.findViewById<View>(R.id.sessionSpeaker) as TextView,
                    view.findViewById(R.id.root)
            )
            view.tag = holder
        }
        val holder = view.tag as ViewHolder
        val (_, name, start, end, trackName, speakerNamesList, isFavorite) = this.sessions[position]
        holder.title.text = name
        val timeString = DateUtil.formatPeriod(start, end)
        holder.time.text = timeString
        holder.place.text = trackName
        val speakerNames = speakerNamesList.orEmpty().joinToString(separator = ", ")
        holder.speaker.text = speakerNames
        if (isFavorite) {
            holder.root.setBackgroundResource(R.drawable.list_item_background_favorite)
        } else {
            holder.root.setBackgroundResource(R.drawable.list_item_background)
        }
        return view
    }

    override fun getHeaderView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: HeaderViewHolder
        if (view == null) {
            view = inflater.inflate(R.layout.agenda_sessions_list_header, parent, false)
            holder = HeaderViewHolder(view as TextView)
            view.tag = holder
        } else {
            holder = view.tag as HeaderViewHolder
        }
        //set header text as first char in name
        val (_, _, start, end) = this.sessions[position]
        val headerText = DateUtil.formatPeriod(start, end)
        holder.text.text = headerText
        return view
    }

    override fun getHeaderId(position: Int): Long {
        val (_, _, start, end) = this.sessions[position]
        return DateUtil.formatPeriod(start, end).hashCode().toLong()
        //        return sessions.get(i).getStart().getTime();
    }

    data class HeaderViewHolder(
            val text: TextView
    )

    data class ViewHolder(
            val title: TextView,
            val time: TextView,
            val place: TextView,
            val speaker: TextView,
            val root: View
    )
}
