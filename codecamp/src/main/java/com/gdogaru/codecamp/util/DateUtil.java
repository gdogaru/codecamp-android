/*
 * Copyright (C) 2008 Gabriel Dogaru (gdogaru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdogaru.codecamp.util;

import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class DateUtil {
    private final static DateTimeFormatter hourFormat = DateTimeFormat.forPattern("HH:mm");
    private final static DateTimeFormatter DATE_DAY_FORMAT = DateTimeFormat.forPattern("EEEE, MMMM dd");
    private final static DateTimeFormatter DATE_DAY_YEAR_FORMAT = DateTimeFormat.forPattern("EEEE, MMMM dd, yyyy");
    private final static DateTimeFormatter EVENT_DATE_FORMAT = DateTimeFormat.forPattern("MMMM dd");

    public static String formatPeriod(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            return "";
        }
        return String.format("%s - %s", formatTime(start), formatTime(end));
    }

    public static String formatEventPeriod(ReadablePartial start, ReadablePartial end) {
        if (start == null && end == null) {
            return "";
        }
        if (start == null) {
            return EVENT_DATE_FORMAT.print(end);
        }
        if (end == null) {
            return EVENT_DATE_FORMAT.print(start);
        }
        if (start.equals(end)) {
            return EVENT_DATE_FORMAT.print(start);
        }
        return String.format("%s - %s, %s", EVENT_DATE_FORMAT.print(start), EVENT_DATE_FORMAT.print(end), end.get(DateTimeFieldType.year()));
    }


    public static String formatTime(LocalTime start) {
        return hourFormat.print(start);
    }

    public static String formatDay(ReadablePartial date) {
        return DATE_DAY_FORMAT.print(date);
    }

    public static String formatPeriod(Date start, Date end) {
        if (start == null || end == null) {
            return "";
        }
        DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        return String.format("%s - %s", DATE_FORMAT.format(start), DATE_FORMAT.format(end));
    }

    public static String formatDayOfYear(ReadablePartial date) {
        return DATE_DAY_YEAR_FORMAT.print(date);
    }
}
