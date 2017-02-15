package com.gdogaru.codecamp.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Gabriel on 2/15/2017.
 */

public class AnalyticsHelper {
    @NonNull
    public static String normalize(@Nullable String input) {
        if (Strings.isNullOrEmpty(input)) return "";
        String s = input.replaceAll(" ", "_").replaceAll("[^A-Za-z0-9_]", "");
        return s.substring(0, Math.min(32, s.length()));
    }
}
