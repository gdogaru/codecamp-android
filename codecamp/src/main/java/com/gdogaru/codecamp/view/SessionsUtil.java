package com.gdogaru.codecamp.view;

import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Track;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionsUtil {

    public static Map<Long, String> extractSpeakers(List<Speaker> speakers) {
        Map<Long, String> result = new HashMap<Long, String>();
        for (Speaker s : speakers) {
            result.put(s.getId(), String.format("%s %s", s.getFirstName(), s.getLastName() == null ? "" : s.getLastName()));
        }
        return result;
    }

    public static Map<Long, String> extractTracks(List<Track> tracks) {
        Map<Long, String> result = new HashMap<Long, String>();
        for (Track s : tracks) {
            result.put(s.getId(), s.getName());
        }
        return result;
    }

}