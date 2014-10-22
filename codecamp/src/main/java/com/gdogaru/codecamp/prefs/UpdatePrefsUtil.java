package com.gdogaru.codecamp.prefs;

import android.content.Context;

public class UpdatePrefsUtil {

    public static final String PREF_NAME = "CODECAMP_UPDATES";
    private static final String LAST_UPDATE = "LAST_UPDATE";
    private static final String LATEST_UPDATES = "LATEST_UPDATES";

    public static long getLastUpdated(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0).getLong(LAST_UPDATE, 0);
    }

    public static long getLastestUpdates(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0).getLong(LATEST_UPDATES, 0);
    }

    public static void setLastUpdated(Context context, Long value) {
        context.getSharedPreferences(PREF_NAME, 0).edit().putLong(LAST_UPDATE, value).commit();
    }

    public static void setLatestUpdates(Context context, Long value) {
        context.getSharedPreferences(PREF_NAME, 0).edit().putLong(LATEST_UPDATES, value).commit();
    }
}
