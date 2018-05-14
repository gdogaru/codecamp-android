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
