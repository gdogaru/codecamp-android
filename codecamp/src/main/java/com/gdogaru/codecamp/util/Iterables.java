package com.gdogaru.codecamp.util;

import java.util.Iterator;

public class Iterables {

    public static <T> int indexOf(Iterable<T> iterable, Predicate<? super T> predicate) {
        return indexOf(iterable.iterator(), predicate);
    }

    public static <T> int indexOf(Iterator<T> iterator, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate, "predicate");
        for (int i = 0; iterator.hasNext(); i++) {
            T current = iterator.next();
            if (predicate.apply(current)) {
                return i;
            }
        }
        return -1;
    }
}
