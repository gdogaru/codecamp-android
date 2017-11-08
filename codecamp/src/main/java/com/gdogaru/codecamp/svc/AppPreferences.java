package com.gdogaru.codecamp.svc;

import android.content.Context;
import android.content.SharedPreferences;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Gabriel on 10/22/2014.
 */
public class AppPreferences {

    public static final String DATA_UPDATE_SETTINGS = "data_update_settings";
    public static final String UPDATING = "updating";
    private static final String LAST_UPDATE = "LAST_UPDATE";
    private static final String LAST_VERSION = "LAST_VERSION";
    private static final String ACTIVE_EVENT = "ACTIVE_EVENT";
    private static final String ACTIVE_SCHEDULE = "ACTIVE_SCHEDULE";
    private static final String LIST_VIEW = "LIST_VIEW";
    private static final Logger LOG = getLogger(AppPreferences.class);
    private final Context app;

    public AppPreferences(Context app) {
        this.app = app;
    }

    private SharedPreferences getPref() {
        return app.getSharedPreferences(UPDATING, Context.MODE_PRIVATE);
    }

    private void clear(Context context) {
        SharedPreferences settings = app.getSharedPreferences(DATA_UPDATE_SETTINGS, Context.MODE_PRIVATE);
        settings.edit()
                .remove(UPDATING)
                .remove(LAST_UPDATE)
                .apply();
    }

    public boolean isUpdating() {
        SharedPreferences settings = getPref();
        return settings.getBoolean(UPDATING, false);
    }

    public void setUpdating(boolean updating) {
        SharedPreferences settings = getPref();
        settings.edit()
                .putBoolean(UPDATING, updating)
                .apply();
    }

    public long getLastUpdated() {
        return getPref().getLong(LAST_UPDATE, 0);
    }

    public void setLastUpdated(Long value) {
        LOG.info("Last update set to {}", value);
        getPref().edit()
                .putLong(LAST_UPDATE, value)
                .apply();
    }

    public long getActiveEvent() {
        return getPref().getLong(ACTIVE_EVENT, 0);
    }

    public void setActiveEvent(Long value) {
        getPref().edit()
                .putLong(ACTIVE_EVENT, value)
                .apply();
    }

    public int getActiveSchedule() {
        return getPref().getInt(ACTIVE_SCHEDULE, 0);
    }

    public void setActiveSchedule(int position) {
        getPref().edit()
                .putInt(ACTIVE_SCHEDULE, position)
                .apply();
    }

    public boolean getListViewList() {
        return getPref().getBoolean(LIST_VIEW, true);
    }

    public void setListViewList(boolean b) {
        getPref().edit()
                .putBoolean(LIST_VIEW, b)
                .apply();
    }

}
