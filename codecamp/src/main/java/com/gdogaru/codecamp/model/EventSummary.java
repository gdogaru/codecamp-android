package com.gdogaru.codecamp.model;

import java.util.Date;

/**
 * Created by Gabriel on 10/2/2016.
 * "refId": 2,
 * "title": "Codecamp Iasi, Autumn Edition 2016",
 * "startDate": "2016-10-22T00:00:00",
 * "endDate": "2016-10-22T00:00:00",
 * "venue": {
 * "name": "Hotel International",
 * "city": "Iasi",
 * "country": "Romania",
 * "directions": "https://goo.gl/maps/neA9CWQnRR42"
 * }
 * }
 */
public class EventSummary {
    private long refId;
    private String title;
    private Date startDate;
    private Date endDate;
    private Venue venue;

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }
}
