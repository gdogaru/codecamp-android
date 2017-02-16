package com.gdogaru.codecamp.svc;

import android.content.Context;
import android.content.SharedPreferences;

import com.gdogaru.codecamp.App;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Gabriel on 2/15/2017.
 */

@Singleton
public class BookmarkingService {
    public static final String BPREFERENECES = "booking_preferencea";

    @Inject
    Gson gson;

    @Inject
    public BookmarkingService() {
    }

    private SharedPreferences getPref() {
        return App.instance().getSharedPreferences(BPREFERENECES, Context.MODE_PRIVATE);
    }

    public Set<String> getBookmarked(String eventId) {
        return getPref().getStringSet(eventId, new HashSet<>());
    }

    public void addBookmarked(String eventId, String itemId) {
        Set<String> s = new HashSet<>(getBookmarked(eventId));
        if (!s.contains(itemId))
            getPref()
                    .edit()
                    .putStringSet(eventId, s)
                    .apply();
    }

    public boolean isBookmarked(String eventId, String itemId) {
        Set<String> s = new HashSet<>(getBookmarked(eventId));
        return s.contains(itemId);
    }

    @SuppressWarnings("unchecked")
    public boolean isBookmarked(String itemId) {
        Map<String, Set<String>> data = (Map<String, Set<String>>) getPref().getAll();
        for (Set<String> s : data.values()) {
            if (s.contains(itemId)) return true;
        }
        return false;
    }

    public void setBookmarked(String title, String itemId, boolean checked) {
        Set<String> s = new HashSet<>(getBookmarked(title));
        if (checked != s.contains(itemId)) {
            if (checked) {
                s.add(itemId);
            } else {
                s.remove(itemId);
            }
            getPref()
                    .edit()
                    .putStringSet(title, s)
                    .apply();
        }
    }
}
