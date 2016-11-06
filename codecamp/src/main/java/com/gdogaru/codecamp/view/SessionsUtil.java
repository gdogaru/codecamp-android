package com.gdogaru.codecamp.view;

import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Track;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionsUtil {

    public static Map<String, String> extractSpeakers(List<Speaker> speakers) {
        Map<String, String> result = new HashMap<String, String>();
        for (Speaker s : speakers) {
            result.put(s.getName(), s.getName());
        }
        return result;
    }

    public static Map<Long, String> extractTrackNames(List<Track> tracks) {
        Map<Long, String> result = new HashMap<Long, String>();
        for (Track s : tracks) {
//            result.put(s.getId(), s.getName());
        }
        return result;
    }

    public static Map<String, String> extractTrackDisplay(List<Track> tracks) {
        Map<String, String> result = new HashMap<String, String>();
        for (Track s : tracks) {
//            String value = s.getNotes() != null && s.getNotes().length() > 0 ? s.getNotes() : s.getName();
//            result.put(s.getId(), value);
        }
        return result;
    }

    public static Map<Long, Track> extractTracks(List<Track> tracks) {
        Map<Long, Track> result = new HashMap<Long, Track>();
        for (Track s : tracks) {
//            result.put(s.getId(), s);
        }
        return result;
    }

}
