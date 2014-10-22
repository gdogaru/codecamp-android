package com.gdogaru.codecamp.svc.jobs;

import android.content.Context;
import android.content.SharedPreferences;

import com.gdogaru.codecamp.CodecampApplication;

/**
 * Created by Gabriel on 10/22/2014.
 */
public class UpdatePrefHelper {

    public static final String DATA_UPDATE_SETTINGS = "data_update_settings";
    public static final String UPDATING = "updating";
    private static final String LAST_UPDATE = "LAST_UPDATE";

    private static  void clear(Context context) {
        SharedPreferences settings = context.getSharedPreferences(DATA_UPDATE_SETTINGS, Context.MODE_PRIVATE);
        settings.edit()
                .remove(UPDATING)
                .remove(LAST_UPDATE)
                .apply();
    }

    public  static boolean isUpdating() {
        SharedPreferences settings = getPref();
        return settings.getBoolean(UPDATING, false);
    }

    private static  SharedPreferences getPref() {
        return CodecampApplication.instance().getSharedPreferences(UPDATING, Context.MODE_PRIVATE);
    }

    public static void setUpdating(boolean updating) {
        SharedPreferences settings = getPref();
        settings.edit()
                .putBoolean(UPDATING, updating)
                .apply();
    }

    public static long getLastUpdated() {
        return getPref().getLong(LAST_UPDATE, 0);
    }

    public static void setLastUpdated( Long value) {
        getPref().edit()
                .putLong(LAST_UPDATE, value)
                .commit();
    }

}
