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

package com.gdogaru.codecamp.util;

import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.TemporalAccessor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
public class DateUtil {
    private final static DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("HH:mm");
    private final static DateTimeFormatter DATE_DAY_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMM dd");
    private final static DateTimeFormatter DATE_DAY_YEAR_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
    private final static DateTimeFormatter EVENT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM dd");

    public static String formatPeriod(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            return "";
        }
        return String.format("%s - %s", formatTime(start), formatTime(end));
    }

    public static String formatEventPeriod(TemporalAccessor start, TemporalAccessor end) {
        if (start == null && end == null) {
            return "";
        }
        if (start == null) {
            return EVENT_DATE_FORMAT.format(end);
        }
        if (end == null) {
            return EVENT_DATE_FORMAT.format(start);
        }
        if (start.equals(end)) {
            return EVENT_DATE_FORMAT.format(start);
        }
        return String.format("%s - %s, %s", EVENT_DATE_FORMAT.format(start), EVENT_DATE_FORMAT.format(end), end.get(ChronoField.YEAR));
    }


    public static String formatTime(LocalTime start) {
        return hourFormat.format(start);
    }

    public static String formatDay(TemporalAccessor date) {
        return DATE_DAY_FORMAT.format(date);
    }

    public static String formatPeriod(Date start, Date end) {
        if (start == null || end == null) {
            return "";
        }
        DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        return String.format("%s - %s", DATE_FORMAT.format(start), DATE_FORMAT.format(end));
    }

    public static String formatDayOfYear(TemporalAccessor date) {
        return DATE_DAY_YEAR_FORMAT.format(date);
    }
}
