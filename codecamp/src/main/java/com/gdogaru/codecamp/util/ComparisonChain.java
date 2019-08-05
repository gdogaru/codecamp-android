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

import androidx.annotation.Nullable;

import java.util.Comparator;

public abstract class ComparisonChain {
    private static final ComparisonChain LESS = new InactiveComparisonChain(-1);
    private static final ComparisonChain GREATER = new InactiveComparisonChain(1);
    private static final ComparisonChain ACTIVE =
            new ComparisonChain() {
                @SuppressWarnings("unchecked")
                @Override
                public ComparisonChain compare(Comparable left, Comparable right) {
                    return classify(left.compareTo(right));
                }

                @Override
                public <T> ComparisonChain compare(
                        @Nullable T left, @Nullable T right, Comparator<T> comparator) {
                    return classify(comparator.compare(left, right));
                }

                @Override
                public ComparisonChain compare(int left, int right) {
                    return classify(Ints.compare(left, right));
                }

                @Override
                public ComparisonChain compare(long left, long right) {
                    return classify(Longs.compare(left, right));
                }

                @Override
                public ComparisonChain compare(float left, float right) {
                    return classify(Float.compare(left, right));
                }

                @Override
                public ComparisonChain compare(double left, double right) {
                    return classify(Double.compare(left, right));
                }

                @Override
                public ComparisonChain compareTrueFirst(boolean left, boolean right) {
                    return classify(Booleans.compare(right, left)); // reversed
                }

                @Override
                public ComparisonChain compareFalseFirst(boolean left, boolean right) {
                    return classify(Booleans.compare(left, right));
                }

                ComparisonChain classify(int result) {
                    return (result < 0) ? LESS : (result > 0) ? GREATER : ACTIVE;
                }

                @Override
                public int result() {
                    return 0;
                }
            };

    private ComparisonChain() {
    }

    /**
     * Begins a new chained comparison statement. See example in the class documentation.
     */
    public static ComparisonChain start() {
        return ACTIVE;
    }

    /**
     * Compares two comparable objects as specified by {@link Comparable#compareTo}, <i>if</i> the
     * result of this comparison chain has not already been determined.
     */
    public abstract ComparisonChain compare(Comparable<?> left, Comparable<?> right);

    /**
     * Compares two objects using a comparator, <i>if</i> the result of this comparison chain has not
     * already been determined.
     */
    public abstract <T> ComparisonChain compare(
            @Nullable T left, @Nullable T right, Comparator<T> comparator);

    /**
     * Compares two {@code int} values as specified by {@link Ints#compare}, <i>if</i> the result of
     * this comparison chain has not already been determined.
     */
    public abstract ComparisonChain compare(int left, int right);

    /**
     * Compares two {@code long} values as specified by {@link Longs#compare}, <i>if</i> the result of
     * this comparison chain has not already been determined.
     */
    public abstract ComparisonChain compare(long left, long right);

    /**
     * Compares two {@code float} values as specified by {@link Float#compare}, <i>if</i> the result
     * of this comparison chain has not already been determined.
     */
    public abstract ComparisonChain compare(float left, float right);

    /**
     * Compares two {@code double} values as specified by {@link Double#compare}, <i>if</i> the result
     * of this comparison chain has not already been determined.
     */
    public abstract ComparisonChain compare(double left, double right);

    /**
     * Discouraged synonym for {@link #compareFalseFirst}.
     *
     * @since 19.0
     * @deprecated Use {@link #compareFalseFirst}; or, if the parameters passed are being either
     * negated or reversed, undo the negation or reversal and use {@link #compareTrueFirst}.
     */
    @Deprecated
    public final ComparisonChain compare(Boolean left, Boolean right) {
        return compareFalseFirst(left, right);
    }

    /**
     * Compares two {@code boolean} values, considering {@code true} to be less than {@code false},
     * <i>if</i> the result of this comparison chain has not already been determined.
     *
     * @since 12.0
     */
    public abstract ComparisonChain compareTrueFirst(boolean left, boolean right);

    /**
     * Compares two {@code boolean} values, considering {@code false} to be less than {@code true},
     * <i>if</i> the result of this comparison chain has not already been determined.
     *
     * @since 12.0 (present as {@code compare} since 2.0)
     */
    public abstract ComparisonChain compareFalseFirst(boolean left, boolean right);

    /**
     * Ends this comparison chain and returns its result: a value having the same sign as the first
     * nonzero comparison result in the chain, or zero if every result was zero.
     */
    public abstract int result();

    private static final class InactiveComparisonChain extends ComparisonChain {
        final int result;

        InactiveComparisonChain(int result) {
            this.result = result;
        }

        @Override
        public ComparisonChain compare(@Nullable Comparable left, @Nullable Comparable right) {
            return this;
        }

        @Override
        public <T> ComparisonChain compare(
                @Nullable T left, @Nullable T right, @Nullable Comparator<T> comparator) {
            return this;
        }

        @Override
        public ComparisonChain compare(int left, int right) {
            return this;
        }

        @Override
        public ComparisonChain compare(long left, long right) {
            return this;
        }

        @Override
        public ComparisonChain compare(float left, float right) {
            return this;
        }

        @Override
        public ComparisonChain compare(double left, double right) {
            return this;
        }

        @Override
        public ComparisonChain compareTrueFirst(boolean left, boolean right) {
            return this;
        }

        @Override
        public ComparisonChain compareFalseFirst(boolean left, boolean right) {
            return this;
        }

        @Override
        public int result() {
            return result;
        }
    }
}