/*
 * Copyright (C) 2008 Gabriel Dogaru (gdogaru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdogaru.codecamp.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
@DatabaseTable(tableName = "codecamp")
public class Codecamp implements Serializable {

    @DatabaseField(id = true)
    @SerializedName("Id")
    long id;
    @DatabaseField
    @SerializedName("Title")
    String title;
    @DatabaseField
    @SerializedName("Description")
    String description;
    @DatabaseField
    @SerializedName("ShortName")
    String shortName;
    @DatabaseField
    @SerializedName("StartDate")
    Date startDate;
    @DatabaseField
    @SerializedName("EndDate")
    Date endDate;

    @SerializedName("Speakers")
    Speaker[] speakers;

    @SerializedName("Tracks")
    Track[] tracks;

    @SerializedName("Sessions")
    Session[] sessions;

    @SerializedName("Location")
    Location location;

    @SerializedName("Sponsors")
    Sponsor[] sponsors;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    public Speaker[] getSpeakers() {
        return speakers;
    }

    public void setSpeakers(Speaker[] speakers) {
        this.speakers = speakers;
    }

    public Track[] getTracks() {
        return tracks;
    }

    public void setTracks(Track[] tracks) {
        this.tracks = tracks;
    }

    public Session[] getSessions() {
        return sessions;
    }

    public void setSessions(Session[] sessions) {
        this.sessions = sessions;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Sponsor[] getSponsors() {
        return sponsors;
    }

    public void setSponsors(Sponsor[] sponsors) {
        this.sponsors = sponsors;
    }
}
