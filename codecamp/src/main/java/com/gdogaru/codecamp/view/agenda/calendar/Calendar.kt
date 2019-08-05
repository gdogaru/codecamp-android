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

package com.gdogaru.codecamp.view.agenda.calendar

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.util.Preconditions
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.io.Serializable
import java.util.*


class Calendar : ScrollView {
    private val context: Activity
    lateinit var parent: RelativeLayout
    lateinit var hourParent: RelativeLayout
    private var PX_PER_MINUTE: Double = 0.toDouble()
    private var HOUR_BAR_WIDTH: Int = 0
    private var events: List<DisplayEvent>? = ArrayList()
    private var startDate: LocalTime? = null
    private var endDate: LocalTime? = null
    private var dateDiff: Long = 0
    private var eventListener: EventListener? = null
    private var currentTime: LocalDateTime? = null
    private var maxInRow = 1
    private var currentTimeLayout: LinearLayout? = null
    var bookmarked: Set<String> = HashSet()
        set(bookmarked) {
            Preconditions.checkNotNull(bookmarked)
            field = bookmarked
            drawEvents()
        }
    private var hz: HorizontalScrollView? = null
    private var scheduleDate: LocalDateTime? = null

    var state: CalendarState
        get() = CalendarState(
                Point(scrollX, scrollY),
                Point(if (hz == null) -1 else hz!!.scrollX, if (hz == null) -1 else hz!!.scrollY)
        )
        set(state) {
            scrollTo(state.vertical.x, state.vertical.y)
            hz!!.scrollTo(state.horizontal.x, state.horizontal.y)
        }

    constructor(context: Context) : super(context) {
        this.context = context as Activity
        initSizes()
        addParent()
        drawEvents()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.context = context as Activity
        initSizes()
        addParent()
        drawEvents()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.context = context as Activity
        initSizes()
        addParent()
    }

    private fun initSizes() {
        HOUR_BAR_WIDTH = dptopx(15.0)
        PX_PER_MINUTE = dptopx(1.9).toDouble()
    }

    internal fun addParent() {
        parent = RelativeLayout(context)
        hourParent = RelativeLayout(context)
        val linearLayout = LinearLayout(context)

        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(linearLayout, lp)
        linearLayout.addView(hourParent)
        hz = HorizontalScrollView(context)
        linearLayout.addView(hz)
        hz!!.addView(parent)
    }

    private fun dptopx(dp: Double): Int {
        val displayMetrics = context.resources.displayMetrics
        return (dp * displayMetrics.density + 0.5).toInt()
    }

    private fun pxToDp(px: Double): Int {
        val r = resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), r.displayMetrics).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private fun drawCurrentTime() {
        //        if (scheduleDate != null) currentTime = scheduleDate.toDateTime();

        if (currentTime == null || scheduleDate == null || events == null || events!!.isEmpty()
                || LocalDate.from(currentTime!!) != scheduleDate!!.toLocalDate()) {
            return
        }
        val lp: RelativeLayout.LayoutParams
        if (currentTimeLayout == null) {
            currentTimeLayout = LinearLayout(context)
            currentTimeLayout!!.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.time_bar))
            lp = RelativeLayout.LayoutParams(HOUR_BAR_WIDTH, dptopx(3.0))
            hourParent.addView(currentTimeLayout, lp)
        } else {
            lp = currentTimeLayout!!.layoutParams as RelativeLayout.LayoutParams
        }
        val (_, _, end) = events!![events!!.size - 1].event
        val last = LocalDateTime.of(LocalDate.now(), end)

        if (currentTime!!.isAfter(last)) return

        val dateDiffMinutes = Duration.between(currentTime, events!![0].event.start.withMinute(0).withSecond(0)).toMinutes() //.get.toDateTimeToday().withMinuteOfHour(0).withSecondOfMinute(0))
        val top = (dateDiffMinutes * PX_PER_MINUTE).toInt()
        lp.setMargins(0, top, 0, 0)

        currentTimeLayout!!.layoutParams = lp
    }


    fun setEvents(events: List<CEvent>) {
        val devs = ArrayList<DisplayEvent>()
        for (ev in events) {
            devs.add(DisplayEvent(ev))
        }
        this.events = devs
        recalculateDisplay()

        drawEvents()
    }

    private fun recalculateDisplay() {
        if (events!!.isEmpty()) {
            return
        }
        Collections.sort(events, EVENT_COMPARATOR)
        //        trimEvents(events);
        startDate = events!![0].event.start
        dateDiff = startDate!!.getMillisOfDay() % MILLIS_IN_DAY - startDate!!.getMillisOfDay() % MILLIS_IN_HOUR
        val (_, _, end) = events!![events!!.size - 1].event
        endDate = end
        //        if (endDate.isBefore(startDate)) {
        //            java.util.Calendar cal = GregorianCalendar.getInstance();
        //            cal.setTime(endDate);
        //            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        //            cal.set(java.util.Calendar.MINUTE, 0);
        //            cal.set(java.util.Calendar.SECOND, 0);
        //            cal.set(java.util.Calendar.MILLISECOND, 0);
        //            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);//next day
        //            cal.add(java.util.Calendar.SECOND, -11);
        //            endDate = cal.getTime();
        //        }

        val bag = HashSet<DisplayEvent>()
        maxInRow = 0
        for (ev in events!!) {
            removeExpired(bag, ev.event.start)
            ev.index = getNextFreeIdx(bag)
            bag.add(ev)
            updateTotals(bag)
            maxInRow = Math.max(maxInRow, bag.size)
        }
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

    private fun updateTotals(bag: Set<DisplayEvent>) {
        var total = bag.size
        for (ev in bag) {
            if (ev.rowTotal > total) {
                total = ev.rowTotal
            }
        }
        for (ev in bag) {
            ev.rowTotal = total
        }
    }

    private fun getNextFreeIdx(bag: Set<DisplayEvent>): Int {
        val idxs = ArrayList<Int>()
        for (i in 0..bag.size) {
            idxs.add(i)
        }
        for (ev in bag) {
            idxs.remove(Integer.valueOf(ev.index))
        }
        return idxs.iterator().next()
    }

    private fun removeExpired(bag: MutableSet<DisplayEvent>, end: LocalTime) {
        val iterator = bag.iterator()
        while (iterator.hasNext()) {
            val ev = iterator.next()
            if (!ev.event.end.isAfter(end)) {
                iterator.remove()
            }
        }
    }

    private fun drawEvents() {
        if (events!!.isEmpty()) {
            return
        }
        recalculateDisplay()

        parent.removeAllViews()

        val screenSize = android.graphics.Point()
        context.windowManager.defaultDisplay.getSize(screenSize)
        var width = screenSize.x - HOUR_BAR_WIDTH
        val neededWidth = maxInRow * dptopx(100.0)
        width = Math.max(width, neededWidth)

        drawHours()

        for (ev in events!!) {
            val pxPerIdx = width / ev.rowTotal
            val layout = LayoutInflater.from(context).inflate(R.layout.c_event_layout2, parent, false)
            val title = layout.findViewById<View>(R.id.title) as TextView
            val desc1 = layout.findViewById<View>(R.id.desc1) as TextView
            val desc2 = layout.findViewById<View>(R.id.desc2) as TextView
            val content = layout.findViewById<View>(R.id.content)

            title.text = trim(ev.event.title)
            desc1.text = ev.event.descLine1
            desc2.text = ev.event.descLine2
            content.setBackgroundResource(if (this.bookmarked.contains(ev.event.id)) R.drawable.list_item_background_favorite else R.drawable.list_item_background)

            var lp: RelativeLayout.LayoutParams? = layout.layoutParams as RelativeLayout.LayoutParams
            if (lp == null) {
                lp = RelativeLayout.LayoutParams(pxPerIdx, (getEventLengthMinutes(ev.event) * PX_PER_MINUTE).toInt())
            }
            val offset = 0// HOUR_BAR_WIDTH;
            lp.setMargins(offset + pxPerIdx * ev.index, (getEventStartDiffMinutes(ev.event) * PX_PER_MINUTE).toInt(), 0, 0)
            lp.width = pxPerIdx
            lp.height = (getEventLengthMinutes(ev.event) * PX_PER_MINUTE).toInt()
            //            lp.setMargins(1, 1, 1, 1);
            layout.setOnClickListener { view -> clicked(ev) }
            parent.addView(layout, lp)
        }

        drawCurrentTime()
    }

    private fun trim(title: String): String {
        return title.replace("<br/>".toRegex(), "\n").replace("\n".toRegex(), ".").replace(" ", "\u00A0")
    }

    private fun clicked(ev: DisplayEvent) {
        if (eventListener != null) {
            eventListener!!.eventCLicked(ev)
        }
    }

    private fun drawHours() {
        val cal = GregorianCalendar()
        //        cal.setTime(startDate);
        val startHour = startDate!!.hour //cal.get(java.util.Calendar.HOUR_OF_DAY);
        //        cal.setTime(endDate);
        var endHour = endDate!!.hour //cal.get(java.util.Calendar.HOUR_OF_DAY);
        if (endHour == 0 || endHour < startHour) {
            endHour = 23
        }
        for (i in startHour..endHour) {
            val layout = LayoutInflater.from(context).inflate(R.layout.c_hour, hourParent, false)
            val tv = layout.findViewById<View>(R.id.text) as TextView
            tv.text = i.toString()
            var lp: RelativeLayout.LayoutParams? = layout.layoutParams as RelativeLayout.LayoutParams
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

    fun setCurrentTime(currentTime: LocalDateTime) {
        this.currentTime = currentTime
    }

    fun updateCurrentTime(date: LocalDateTime) {
        currentTime = date
        drawCurrentTime()
    }

    fun setScheduleDate(scheduleDate: LocalDateTime) {
        this.scheduleDate = scheduleDate
    }


    private class DisplayEventComparator : Comparator<DisplayEvent> {
        override fun compare(e1: DisplayEvent, e2: DisplayEvent): Int {
            val r1 = e1.event.start.compareTo(e2.event.start)
            return if (r1 != 0)
                r1
            else
                e1.event.preferedIdx - e2.event.preferedIdx
        }
    }


    class Point(val x: Int, val y: Int) : Serializable

    class CalendarState(val vertical: Point, val horizontal: Point) : Serializable

    companion object {
        private val EVENT_COMPARATOR = DisplayEventComparator()
        private val MINUTES_IN_DAY = 1440
        private val MILLIS_IN_MINUTE: Long = 60000
        private val MILLIS_IN_DAY: Long = 86400000
        private val MILLIS_IN_HOUR: Long = 3600000
    }
}

interface EventListener {
    fun eventCLicked(event: DisplayEvent)
}

private fun LocalTime.getMillisOfDay(): Long {
    return Duration.between(LocalTime.MIN, this).get(org.threeten.bp.temporal.ChronoUnit.SECONDS) * 1000
}
