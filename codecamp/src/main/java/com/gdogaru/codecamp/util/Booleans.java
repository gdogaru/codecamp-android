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

public class Booleans {
    /**
     * Compares the two specified {@code boolean} values in the standard way ({@code false} is
     * considered less than {@code true}). The sign of the value returned is the same as that of
     * {@code ((Boolean) a).compareTo(b)}.
     * <p>
     * <p><b>Note for Java 7 and later:</b> this method should be treated as deprecated; use the
     * equivalent {@link Boolean#compare} method instead.
     *
     * @param a the first {@code boolean} to compare
     * @param b the second {@code boolean} to compare
     * @return a positive number if only {@code a} is {@code true}, a negative number if only {@code
     * b} is true, or zero if {@code a == b}
     */
    public static int compare(boolean a, boolean b) {
        return (a == b) ? 0 : (a ? 1 : -1);
    }

}
