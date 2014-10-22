package com.gdogaru.codecamp.util;

public class Preconditions {

    public static void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException("Null variable " + name);
        }
    }
}
