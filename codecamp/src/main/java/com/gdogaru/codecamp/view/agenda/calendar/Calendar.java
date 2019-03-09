package com.gdogaru.codecamp.view.agenda.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.util.Preconditions;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


public class Calendar extends ScrollView {
    private static final Comparator<? super DisplayEvent> EVENT_COMPARATOR = new DisplayEventComparator();
    private static final int MINUTES_IN_DAY = 1440;
    private static final long MILLIS_IN_MINUTE = 60000;
    private static final long MILLIS_IN_DAY = 86400000;
    private static final long MILLIS_IN_HOUR = 3600000;
    private final Activity context;
    RelativeLayout parent;
    RelativeLayout hourParent;
    private double PX_PER_MINUTE;
    private int HOUR_BAR_WIDTH;
    private List<DisplayEvent> events = new ArrayList<>();
    private LocalTime startDate;
    private LocalTime endDate;
    private long dateDiff;
    private EventListener eventListener;
    private DateTime currentTime;
    private int maxInRow = 1;
    private LinearLayout currentTimeLayout;
    private Set<String> bookmarked = new HashSet<>();
    private HorizontalScrollView hz;
    private LocalDateTime scheduleDate;

    public Calendar(Context context) {
        super(context);
        this.context = (Activity) context;
        initSizes();
        addParent();
        drawEvents();
    }

    public Calendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
        initSizes();
        addParent();
        drawEvents();
    }

    public Calendar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = (Activity) context;
        initSizes();
        addParent();
    }

    public CalendarState getState() {
        return new CalendarState(
                new Point(getScrollX(), getScrollY()),
                new Point(hz == null ? -1 : hz.getScrollX(), hz == null ? -1 : hz.getScrollY())
        );
    }

    public void setState(CalendarState state) {
        scrollTo(state.vertical.x, state.vertical.y);
        hz.scrollTo(state.horizontal.x, state.horizontal.y);
    }

    private void initSizes() {
        HOUR_BAR_WIDTH = dptopx(15);
        PX_PER_MINUTE = dptopx(1.9);
    }

    void addParent() {
        parent = new RelativeLayout(context);
        hourParent = new RelativeLayout(context);
        LinearLayout linearLayout = new LinearLayout(context);

        ScrollView.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(linearLayout, lp);
        linearLayout.addView(hourParent);
        hz = new HorizontalScrollView(context);
        linearLayout.addView(hz);
        hz.addView(parent);
    }

    private int dptopx(double dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    private int pxToDp(double px) {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) px, r.getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void drawCurrentTime() {
//        if (scheduleDate != null) currentTime = scheduleDate.toDateTime();

        if (currentTime == null || scheduleDate == null || events == null || events.isEmpty()
                || !currentTime.toLocalDate().equals(scheduleDate.toLocalDate())) {
            return;
        }
        RelativeLayout.LayoutParams lp;
        if (currentTimeLayout == null) {
            currentTimeLayout = new LinearLayout(context);
            currentTimeLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.time_bar));
            lp = new RelativeLayout.LayoutParams(HOUR_BAR_WIDTH, dptopx(3));
            hourParent.addView(currentTimeLayout, lp);
        } else {
            lp = (RelativeLayout.LayoutParams) currentTimeLayout.getLayoutParams();
        }
        CEvent lastEvent = events.get(events.size() - 1).event;
        DateTime last = lastEvent.end.toDateTimeToday();

        if (currentTime.isAfter(last)) return;

        int dateDiffMinutes = currentTime.minuteOfDay().getDifference(events.get(0).event.start.toDateTimeToday().withMinuteOfHour(0).withSecondOfMinute(0));
        int top = (int) (dateDiffMinutes * PX_PER_MINUTE);
        lp.setMargins(0, top, 0, 0);

        currentTimeLayout.setLayoutParams(lp);
    }


    public void setEvents(List<CEvent> events) {
        List<DisplayEvent> devs = new ArrayList<>();
        for (CEvent ev : events) {
            devs.add(new DisplayEvent(ev));
        }
        this.events = devs;
        recalculateDisplay();

        drawEvents();
    }

    private void recalculateDisplay() {
        if (events.isEmpty()) {
            return;
        }
        Collections.sort(events, EVENT_COMPARATOR);
//        trimEvents(events);
        startDate = events.get(0).event.start;
        dateDiff = startDate.getMillisOfDay() % MILLIS_IN_DAY - startDate.getMillisOfDay() % MILLIS_IN_HOUR;
        CEvent lastEvent = events.get(events.size() - 1).event;
        endDate = lastEvent.end;
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

        Set<DisplayEvent> bag = new HashSet<DisplayEvent>();
        maxInRow = 0;
        for (DisplayEvent ev : events) {
            removeExpired(bag, ev.event.start);
            ev.index = getNextFreeIdx(bag);
            bag.add(ev);
            updateTotals(bag);
            maxInRow = Math.max(maxInRow, bag.size());
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

    private void updateTotals(Set<DisplayEvent> bag) {
        int total = bag.size();
        for (DisplayEvent ev : bag) {
            if (ev.rowTotal > total) {
                total = ev.rowTotal;
            }
        }
        for (DisplayEvent ev : bag) {
            ev.rowTotal = total;
        }
    }

    private int getNextFreeIdx(Set<DisplayEvent> bag) {
        List<Integer> idxs = new ArrayList<Integer>();
        for (int i = 0; i <= bag.size(); i++) {
            idxs.add(i);
        }
        for (DisplayEvent ev : bag) {
            idxs.remove(Integer.valueOf(ev.index));
        }
        return idxs.iterator().next();
    }

    private void removeExpired(Set<DisplayEvent> bag, LocalTime end) {
        for (Iterator<DisplayEvent> iterator = bag.iterator(); iterator.hasNext(); ) {
            DisplayEvent ev = iterator.next();
            if (!ev.event.end.isAfter(end)) {
                iterator.remove();
            }
        }
    }

    private void drawEvents() {
        if (events.isEmpty()) {
            return;
        }
        recalculateDisplay();

        parent.removeAllViews();

        android.graphics.Point screenSize = new android.graphics.Point();
        context.getWindowManager().getDefaultDisplay().getSize(screenSize);
        int width = screenSize.x - HOUR_BAR_WIDTH;
        int neededWidth = maxInRow * dptopx(100);
        width = Math.max(width, neededWidth);

        drawHours();

        for (final DisplayEvent ev : events) {
            int pxPerIdx = width / ev.rowTotal;
            View layout = LayoutInflater.from(context).inflate(R.layout.c_event_layout2, parent, false);
            TextView title = (TextView) layout.findViewById(R.id.title);
            TextView desc1 = (TextView) layout.findViewById(R.id.desc1);
            TextView desc2 = (TextView) layout.findViewById(R.id.desc2);
            View content = layout.findViewById(R.id.content);

            title.setText(trim(ev.event.title));
            desc1.setText(ev.event.descLine1);
            desc2.setText(ev.event.descLine2);
            content.setBackgroundResource(bookmarked.contains(ev.event.id) ? R.drawable.list_item_background_favorite : R.drawable.list_item_background);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            if (lp == null) {
                lp = new RelativeLayout.LayoutParams(pxPerIdx, (int) (getEventLengthMinutes(ev.event) * PX_PER_MINUTE));
            }
            int offset = 0;// HOUR_BAR_WIDTH;
            lp.setMargins(offset + (int) (pxPerIdx * ev.index), (int) (getEventStartDiffMinutes(ev.event) * PX_PER_MINUTE), 0, 0);
            lp.width = pxPerIdx;
            lp.height = (int) (getEventLengthMinutes(ev.event) * PX_PER_MINUTE);
//            lp.setMargins(1, 1, 1, 1);
            layout.setOnClickListener(view -> clicked(ev));
            parent.addView(layout, lp);
        }

        drawCurrentTime();
    }

    private String trim(String title) {
        return title.replaceAll("<br/>", "\n").replaceAll("\n", ".").replace(" ", "\u00A0");
    }

    private void clicked(DisplayEvent ev) {
        if (eventListener != null) {
            eventListener.eventCLicked(ev);
        }
    }

    private void drawHours() {
        java.util.Calendar cal = new GregorianCalendar();
//        cal.setTime(startDate);
        int startHour = startDate.getHourOfDay(); //cal.get(java.util.Calendar.HOUR_OF_DAY);
//        cal.setTime(endDate);
        int endHour = endDate.getHourOfDay(); //cal.get(java.util.Calendar.HOUR_OF_DAY);
        if (endHour == 0 || endHour < startHour) {
            endHour = 23;
        }
        for (int i = startHour; i <= endHour; i++) {
            View layout = LayoutInflater.from(context).inflate(R.layout.c_hour, null);
            TextView tv = (TextView) layout.findViewById(R.id.text);
            tv.setText(String.valueOf(i));
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            if (lp == null) {
                lp = new RelativeLayout.LayoutParams(HOUR_BAR_WIDTH, (int) (PX_PER_MINUTE * 60));
            } else {
                lp.width = HOUR_BAR_WIDTH;
                lp.height = (int) (PX_PER_MINUTE * 60);
            }
            lp.setMargins(0, (int) (PX_PER_MINUTE * 60 * (i - startHour)), 0, 0);
            layout.setLayoutParams(lp);
            hourParent.addView(layout, lp);
        }
    }

    private int getEventStartDiffMinutes(CEvent event) {
        return (int) (((event.start.millisOfDay().get() % MILLIS_IN_DAY) - dateDiff) / MILLIS_IN_MINUTE);
    }

    private int getEventLengthMinutes(CEvent event) {
        return (int) ((event.end.millisOfDay().get() - event.start.millisOfDay().get()) / MILLIS_IN_MINUTE);
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setCurrentTime(DateTime currentTime) {
        this.currentTime = currentTime;
    }

    public void updateCurrentTime(DateTime date) {
        currentTime = date;
        drawCurrentTime();
    }

    public Set<String> getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(@NonNull Set<String> bookmarked) {
        Preconditions.checkNotNull(bookmarked);
        this.bookmarked = bookmarked;
        drawEvents();
    }

    public void setScheduleDate(LocalDateTime scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public interface EventListener {
        void eventCLicked(DisplayEvent event);
    }

    private static class DisplayEventComparator implements Comparator<DisplayEvent> {
        @Override
        public int compare(DisplayEvent e1, DisplayEvent e2) {
            int r1 = e1.event.start.compareTo(e2.event.start);
            return r1 != 0
                    ? r1
                    : e1.event.preferedIdx - e2.event.preferedIdx;
        }
    }


    private static class Point implements Serializable {
        public final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class CalendarState implements Serializable {
        final public Point vertical;
        final public Point horizontal;

        public CalendarState(Point vertical, Point horizontal) {
            this.vertical = vertical;
            this.horizontal = horizontal;
        }
    }
}
