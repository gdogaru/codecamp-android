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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class DateUtil {


    public static String formatPeriod(Date start, Date end) {
        if (start == null || end == null) {
            return "";
        }
        DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        return String.format("%s - %s", DATE_FORMAT.format(start), DATE_FORMAT.format(end));
    }
}
