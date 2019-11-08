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

package com.gdogaru.codecamp.view.agenda.calendar.component

import android.content.Context
import android.graphics.Point
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.gdogaru.codecamp.R
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.util.*
import kotlin.math.max


class Calendar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private lateinit var parent: RelativeLayout
    private lateinit var hourParent: RelativeLayout
    private lateinit var hz: HorizontalScrollView
    private val PX_PER_MINUTE: Double
    private val HOUR_BAR_WIDTH: Int
    private var events: List<DisplayEvent> = ArrayList()
    private var startDate: LocalTime = LocalTime.now()
    private var endDate: LocalTime = LocalTime.now()
    private var startHour = 9
    private var endHour = 23
    private var currentTime: LocalDateTime = LocalDateTime.now()
    private var dateDiff: Long = 0
    private var maxInRow = 1
    private var eventListener: EventListener? = null

    private var currentTimeLayout: View? = null

    private var scheduleDate: LocalDateTime? = null

    var state: CalendarState
        get() = CalendarState(
            Point(scrollX, scrollY),
            Point(hz.scrollX, hz.scrollY)
        )
        set(state) {
            scrollTo(state.vertical.x, state.vertical.y)
            hz.scrollTo(state.horizontal.x, state.horizontal.y)
        }

    init {
        HOUR_BAR_WIDTH = dptopx(15.0)
        PX_PER_MINUTE = dptopx(1.9).toDouble()

        addParent()
        drawEvents()
    }

    private fun addParent() {
        parent = RelativeLayout(context)
        hourParent = RelativeLayout(context)
        val linearLayout = LinearLayout(context)

        val lp =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(linearLayout, lp)
        linearLayout.addView(hourParent)
        hz = HorizontalScrollView(context)
        linearLayout.addView(hz)
        hz.addView(parent)
    }

    private fun dptopx(dp: Double): Int {
        val displayMetrics = context.resources.displayMetrics
        return (dp * displayMetrics.density + 0.5).toInt()
    }

    private fun drawCurrentTime() {
        if (scheduleDate == null || events.isEmpty()
            || LocalDate.from(currentTime) != scheduleDate!!.toLocalDate()
        ) {
            return
        }

        val timeLayout = currentTimeLayout ?: addCurrentTimeLayout()

        val (_, _, end) = events[events.size - 1].event
        val last = LocalDateTime.of(LocalDate.now(), end)

        if (currentTime.isAfter(last) || currentTime.hour < startHour) return

        val firstEventTime = LocalDateTime.of(
            currentTime.toLocalDate(),
            events[0].event.start.withMinute(0).withSecond(0)
        )
        val dateDiffMinutes = Duration.between(firstEventTime, currentTime).toMinutes()

        val top = (dateDiffMinutes * PX_PER_MINUTE).toInt()

        timeLayout.updateLayoutParams<RelativeLayout.LayoutParams> { setMargins(0, top, 0, 0) }

    }

    private fun addCurrentTimeLayout() = View(context).also {
        it.setBackgroundColor(ContextCompat.getColor(context, R.color.time_bar))
        val lp = RelativeLayout.LayoutParams(HOUR_BAR_WIDTH, dptopx(3.0))
        hourParent.addView(it, lp)
        currentTimeLayout = it
    }

    fun setEvents(scheduleDate: LocalDateTime, eventList: List<CEvent>) {
        this.scheduleDate = scheduleDate
        this.events = eventList.map { DisplayEvent(it) }

        recalculateDisplay()
        drawEvents()
    }

    private fun recalculateDisplay() {
        Collections.sort(events, EVENT_COMPARATOR)
        //        trimEvents(events);

        startDate = if (events.isNotEmpty()) {
            events[0].event.start
        } else {
            LocalTime.NOON.withHour(9)
        }

        endDate = if (events.isNotEmpty()) {
            events[events.size - 1].event.end
        } else {
            LocalTime.NOON.withHour(21)
        }

        startHour = startDate.hour
        endHour = endDate.hour

        dateDiff =
            startDate.getMillisOfDay() % MILLIS_IN_DAY - startDate.getMillisOfDay() % MILLIS_IN_HOUR

        maxInRow = calculateMaxRowSize(events)
    }

    private fun calculateMaxRowSize(events: List<DisplayEvent>): Int {
        val bag = mutableSetOf<DisplayEvent>()
        maxInRow = 0
        for (ev in events) {
            //remove events that end before current start
            bag.removeAll { !it.event.end.isAfter(ev.event.start) }
            //set index to last element or missing index if gaps
            ev.index = (0..bag.size).toMutableSet()
                .also { s -> s.removeAll(bag.map { it.index }) }
                .first()
            bag.add(ev)
            bag.forEach { it.rowTotal = bag.size }
            maxInRow = max(maxInRow, bag.size)
        }
        return maxInRow
    }

    //    /**
    //     * Removes events not in the same day
    //     *
    //     * @param events
    //     */
    //    private void trimEvents(List<DisplayEvent> events) {
    //        java.util.Calendar cal = new GregorianCalendar();
    //        cal.setTime(events.get(0).event.start);
    //        int day = cal.get(java.util.Calendar.DAY_OF_WEEK);
    //        long sm = events.get(0).event.start.getTime();
    //        TimeZone tz = TimeZone.getDefault();
    //        Date eod = new Date(sm - sm % MILLIS_IN_DAY + MILLIS_IN_DAY - 1 - tz.getOffset(cal.getTime().getTime()));
    //        for (Iterator<DisplayEvent> iterator = events.iterator(); iterator.hasNext(); ) {
    //            DisplayEvent e = iterator.next();
    //            cal.setTime(e.event.start);
    //            if (cal.get(java.util.Calendar.DAY_OF_WEEK) != day) {
    //                iterator.remove();
    //            } else {
    //                cal.setTime(e.event.end);
    //                if (cal.get(java.util.Calendar.DAY_OF_WEEK) != day) {
    //                    e.event.end.setTime(eod.getTime());
    //                }
    //            }
    //        }
    //    }

    private fun drawEvents() {
        recalculateDisplay()

        parent.removeAllViews()

        val screenSize = getScreenSize()
        var width = screenSize.x - HOUR_BAR_WIDTH
        val neededWidth = maxInRow * dptopx(100.0)
        width = max(width, neededWidth)

        drawHours()

        for (ev in events) {
            val pxPerIdx = width / ev.rowTotal
            val layout =
                LayoutInflater.from(context)
                    .inflate(R.layout.calendar_event, parent, false)
            val title = layout.findViewById<View>(R.id.title) as TextView
            val desc1 = layout.findViewById<View>(R.id.desc1) as TextView
            val desc2 = layout.findViewById<View>(R.id.desc2) as TextView
            val content = layout.findViewById<View>(R.id.content)

            title.text = trim(ev.event.title)
            desc1.text = ev.event.descLine1
            desc2.text = ev.event.descLine2
            content.setBackgroundResource(if (ev.event.bookmarked) R.drawable.list_item_background_favorite else R.drawable.list_item_background)

            val lp: RelativeLayout.LayoutParams =
                layout.layoutParams as RelativeLayout.LayoutParams?
                    ?: RelativeLayout.LayoutParams(
                        pxPerIdx, (getEventLengthMinutes(ev.event) * PX_PER_MINUTE).toInt()
                    )

            val offset = 0// HOUR_BAR_WIDTH;
            lp.setMargins(
                offset + pxPerIdx * ev.index,
                (getEventStartDiffMinutes(ev.event) * PX_PER_MINUTE).toInt(),
                0,
                0
            )
            lp.width = pxPerIdx
            lp.height = (getEventLengthMinutes(ev.event) * PX_PER_MINUTE).toInt()
            //            lp.setMargins(1, 1, 1, 1);
            layout.setOnClickListener { clicked(ev) }
            parent.addView(layout, lp)
        }

        drawCurrentTime()
    }

    private fun getScreenSize() = Point().also {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getSize(it)
    }


    private fun trim(title: String): String {
        return title.replace("<br/>".toRegex(), "\n")
            .replace("\n".toRegex(), ".")
            .replace(" ", "\u00A0")
    }

    private fun clicked(ev: DisplayEvent) {
        eventListener?.eventCLicked(ev)
    }

    private fun drawHours() {
        if (endHour == 0 || endHour < startHour) {
            endHour = 23
        }
        hourParent.removeAllViews()
        for (i in startHour..endHour) {
            val layout =
                LayoutInflater.from(context)
                    .inflate(R.layout.calendar_hour, hourParent, false)
            val tv = layout.findViewById<View>(R.id.text) as TextView
            tv.text = i.toString()
            var lp: RelativeLayout.LayoutParams? =
                layout.layoutParams as RelativeLayout.LayoutParams
            if (lp == null) {
                lp = RelativeLayout.LayoutParams(HOUR_BAR_WIDTH, (PX_PER_MINUTE * 60).toInt())
            } else {
                lp.width = HOUR_BAR_WIDTH
                lp.height = (PX_PER_MINUTE * 60).toInt()
            }
            lp.setMargins(0, (PX_PER_MINUTE * 60.0 * (i - startHour).toDouble()).toInt(), 0, 0)
            layout.layoutParams = lp
            hourParent.addView(layout, lp)
        }

        currentTimeLayout?.let { hourParent.addView(it) }
    }

    private fun getEventStartDiffMinutes(event: CEvent): Long {
        return (event.start.getMillisOfDay() % MILLIS_IN_DAY - dateDiff) / MILLIS_IN_MINUTE
    }

    private fun getEventLengthMinutes(event: CEvent): Int {
        return ((event.end.getMillisOfDay() - event.start.getMillisOfDay()) / MILLIS_IN_MINUTE).toInt()
    }

    fun setEventListener(eventListener: EventListener) {
        this.eventListener = eventListener
    }

    fun updateCurrentTime(date: LocalDateTime) {
        currentTime = date
        drawCurrentTime()
    }

    private class DisplayEventComparator : Comparator<DisplayEvent> {
        override fun compare(e1: DisplayEvent, e2: DisplayEvent): Int {
            val r1 = e1.event.start.compareTo(e2.event.start)
            return if (r1 != 0)
                r1
            else
                e1.event.preferredIdx - e2.event.preferredIdx
        }
    }

    @Parcelize
    class CalendarState(
        val vertical: Point,
        val horizontal: Point
    ) : Parcelable

    companion object {
        private val EVENT_COMPARATOR = DisplayEventComparator()
        private const val MINUTES_IN_DAY = 1440
        private const val MILLIS_IN_MINUTE: Long = 60000
        private const val MILLIS_IN_DAY: Long = 86400000
        private const val MILLIS_IN_HOUR: Long = 3600000
    }
}

interface EventListener {
    fun eventCLicked(event: DisplayEvent)
}

private fun LocalTime.getMillisOfDay(): Long {
    return Duration.between(
        LocalTime.MIN,
        this
    ).get(org.threeten.bp.temporal.ChronoUnit.SECONDS) * 1000
}
