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

    private void clear(Context context) {
        SharedPreferences settings = context.getSharedPreferences(DATA_UPDATE_SETTINGS, Context.MODE_PRIVATE);
        settings.edit()
                .remove(UPDATING)
                .apply();
    }

    private boolean isUpdating() {
        SharedPreferences settings = CodecampApplication.instance().getSharedPreferences(UPDATING, Context.MODE_PRIVATE);
        return settings.getBoolean(UPDATING, false);
    }

    private void setUpdating(boolean updating) {
        SharedPreferences settings = CodecampApplication.instance().getSharedPreferences(UPDATING, Context.MODE_PRIVATE);
        settings.edit()
                .putBoolean(UPDATING, updating)
                .apply();
    }

}
