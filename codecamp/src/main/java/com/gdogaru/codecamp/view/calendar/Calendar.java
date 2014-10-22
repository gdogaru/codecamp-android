package com.gdogaru.codecamp.view.calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gdogaru.codecamp.R;

import java.util.*;


public class Calendar extends ScrollView {
    private static final Comparator<? super DisplayEvent> EVENT_COMPARATOR = new DisplayEventComparator();
    private static final int MINUTES_IN_DAY = 1440;
    private static final long MILLIS_IN_MINUTE = 60000;
    private static final long MILLIS_IN_DAY = 86400000;
    private static final long MILLIS_IN_HOUR = 3600000;
    private final double PX_PER_MINUTE;
    private final int HOUR_BAR_WIDTH;
    private final Activity context;
    private List<DisplayEvent> events = new ArrayList<DisplayEvent>();
    private Date startDate;
    private Date endDate;
    private long dateDiff;
    private EventListener eventListener;
    private Date currentTime;

    RelativeLayout parent;

    public Calendar(Context context) {
        super(context);
        this.context = (Activity) context;
        HOUR_BAR_WIDTH = dptopx(15);
        PX_PER_MINUTE = dptopx(1.7);
        addParent();

        drawEvents();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                }
                return false;
            }
        });
    }

    public Calendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
        HOUR_BAR_WIDTH = dptopx(15);
        PX_PER_MINUTE = dptopx(1.7);
        addParent();
        drawEvents();
    }

    public Calendar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = (Activity) context;
        HOUR_BAR_WIDTH = dptopx(15);
        PX_PER_MINUTE = dptopx(1.7);
        addParent();
    }

    void addParent() {
        parent = new RelativeLayout(context);
        ScrollView.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(parent, lp);
    }

    private int dptopx(double dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void drawCurrentTime() {
        if (currentTime == null || startDate.getTime() / MILLIS_IN_DAY != currentTime.getTime() / MILLIS_IN_DAY) {
            return;
        }

        View layout = new LinearLayout(context);
        layout.setBackgroundColor(getResources().getColor(R.color.time_bar));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dptopx(2));

        int top = (int) (getDateStartDiffMinutes(currentTime) * PX_PER_MINUTE);
        lp.setMargins(0, top, 0, 0);

        parent.addView(layout, lp);

        final int y = (int) (top - PX_PER_MINUTE * 60);
        if (top > 0) {
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollTo(0, y);
                }
            }, 500);
        }
    }

    private int getDateStartDiffMinutes(Date date) {
        return (int) (((date.getTime() % MILLIS_IN_DAY) - dateDiff) / MILLIS_IN_MINUTE);
    }


    public void setEvents(List<CEvent> events) {
        List<DisplayEvent> devs = new ArrayList<DisplayEvent>();
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
        trimEvents(events);
        startDate = events.get(0).event.start;
        dateDiff = startDate.getTime() % MILLIS_IN_DAY - startDate.getTime() % MILLIS_IN_HOUR;
        endDate = events.get(events.size() - 1).event.end;
        if (endDate.before(startDate)) {
            java.util.Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(endDate);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);//next day
            cal.add(java.util.Calendar.SECOND, -11);
            endDate = cal.getTime();
        }

        Set<DisplayEvent> bag = new HashSet<DisplayEvent>();
        for (DisplayEvent ev : events) {
            removeExpired(bag, ev.event.start);
            ev.index = getNextFreeIdx(bag);
            bag.add(ev);
            updateTotals(bag);
        }
    }

    /**
     * Removes events not in the same day
     *
     * @param events
     */
    private void trimEvents(List<DisplayEvent> events) {
        java.util.Calendar cal = new GregorianCalendar();
        cal.setTime(events.get(0).event.start);
        int day = cal.get(java.util.Calendar.DAY_OF_WEEK);
        long sm = events.get(0).event.start.getTime();
        TimeZone tz = TimeZone.getDefault();
        Date eod = new Date(sm - sm % MILLIS_IN_DAY + MILLIS_IN_DAY - 1 - tz.getOffset(cal.getTime().getTime()));
        for (Iterator<DisplayEvent> iterator = events.iterator(); iterator.hasNext(); ) {
            DisplayEvent e = iterator.next();
            cal.setTime(e.event.start);
            if (cal.get(java.util.Calendar.DAY_OF_WEEK) != day) {
                iterator.remove();
            } else {
                cal.setTime(e.event.end);
                if (cal.get(java.util.Calendar.DAY_OF_WEEK) != day) {
                    e.event.end.setTime(eod.getTime());
                }
            }
        }
    }

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
            idxs.remove(new Integer(ev.index));
        }
        return idxs.iterator().next();
    }

    private void removeExpired(Set<DisplayEvent> bag, Date end) {
        for (Iterator<DisplayEvent> iterator = bag.iterator(); iterator.hasNext(); ) {
            DisplayEvent ev = iterator.next();
            if (!ev.event.end.after(end)) {
                iterator.remove();
            }
        }
    }

    private void drawEvents() {
        if (events.isEmpty()) {
            return;
        }
        recalculateDisplay();
        int width = getWidth();

        width = context.getWindowManager().getDefaultDisplay().getWidth() - HOUR_BAR_WIDTH;

        drawHours();

        for (final DisplayEvent ev : events) {
            int pxPerIdx = width / ev.rowTotal;
            View layout = LayoutInflater.from(context).inflate(R.layout.c_event_layout, null);
            TextView tv = (TextView) layout.findViewById(R.id.title);
            tv.setText(ev.event.title);

            tv = (TextView) layout.findViewById(R.id.desc1);
            tv.setText(ev.event.descLine1);

            tv = (TextView) layout.findViewById(R.id.desc2);
            tv.setText(ev.event.descLine2);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            if (lp == null) {
                lp = new RelativeLayout.LayoutParams(pxPerIdx, (int) (getEventLengthMinutes(ev.event) * PX_PER_MINUTE));
            }
            lp.setMargins(HOUR_BAR_WIDTH + (int) (pxPerIdx * ev.index), (int) (getEventStartDiffMinutes(ev.event) * PX_PER_MINUTE), 0, 0);
            lp.width = pxPerIdx;
            lp.height = (int) (getEventLengthMinutes(ev.event) * PX_PER_MINUTE);
//            lp.setMargins(1, 1, 1, 1);
            layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(ev);
                }
            });
            parent.addView(layout, lp);
        }

        drawCurrentTime();
    }

    private void clicked(DisplayEvent ev) {
        if (eventListener != null) {
            eventListener.eventCLicked(ev);
        }
    }

    private void drawHours() {
        java.util.Calendar cal = new GregorianCalendar();
        cal.setTime(startDate);
        int startHour = cal.get(java.util.Calendar.HOUR_OF_DAY);
        cal.setTime(endDate);
        int endHour = cal.get(java.util.Calendar.HOUR_OF_DAY);
        if (endHour == 0) {
            endHour = 23;
        }
        for (int i = startHour; i <= endHour; i++) {
            View layout = LayoutInflater.from(context).inflate(R.layout.c_hour, null);
            TextView tv = (TextView) layout.findViewById(R.id.text);
            tv.setText(i + "");
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            if (lp == null) {
                lp = new RelativeLayout.LayoutParams(HOUR_BAR_WIDTH, (int) (PX_PER_MINUTE * 60));
            } else {
                lp.width = HOUR_BAR_WIDTH;
                lp.height = (int) (PX_PER_MINUTE * 60);
            }
            lp.setMargins(0, (int) (PX_PER_MINUTE * 60 * (i - startHour)), 0, 0);
            layout.setLayoutParams(lp);
            parent.addView(layout, lp);
        }
    }

    private int getEventStartDiffMinutes(CEvent event) {
        return (int) (((event.start.getTime() % MILLIS_IN_DAY) - dateDiff) / MILLIS_IN_MINUTE);
    }

    private int getEventLengthMinutes(CEvent event) {
        return (int) ((event.end.getTime() - event.start.getTime()) / MILLIS_IN_MINUTE);
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
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
}
