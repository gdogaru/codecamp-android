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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

public class AnalyticsHelper {
    @NonNull
    public static String normalize(@Nullable String input) {
        if (Strings.isNullOrEmpty(input)) return "";
        String s = input.replaceAll(" ", "_").replaceAll("[^A-Za-z0-9_]", "");
        return s.substring(0, Math.min(32, s.length()));
    }
}
