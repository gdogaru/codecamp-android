package com.gdogaru.codecamp.view.sessions;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class SessionListItem {

    private long id;
    private String name;
    private Date start;
    public static Comparator<SessionListItem> SESSION_BY_DATE_COMPARATOR = new Comparator<SessionListItem>() {

        @Override
        public int compare(SessionListItem object1, SessionListItem object2) {
            return object1.getStart() == null ? -1 : object1.getStart().compareTo(object2.getStart());
        }
    };
    private Date end;
    private String trackName;
    private List<String> speakerNames;
    private Long trackId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public List<String> getSpeakerNames() {
        return speakerNames;
    }

    public void setSpeakerNames(List<String> speakerNames) {
        this.speakerNames = speakerNames;
    }

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }
}
