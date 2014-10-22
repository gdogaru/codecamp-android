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
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Gabriel Dogaru (gdogaru@DatabaseField    @gmail.com)
 */
@DatabaseTable(tableName = "sessions")
public class Session implements Serializable {

    @DatabaseField(generatedId = true, columnName = "_id")
    @SerializedName("Id")
    long id;
    @DatabaseField
    @SerializedName("Title")
    String title;
    @DatabaseField
    @SerializedName("Description")
    String description;
    @DatabaseField
    @SerializedName("OverrideTracks")
    boolean overrideTracks;
    @DatabaseField
    @SerializedName("Start")
    Date start;
    @DatabaseField
    @SerializedName("End")
    Date end;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    @SerializedName("SpeakerRefIds")
    Long[] speakerRefIds;
    @DatabaseField
    @SerializedName("TrackRefId")
    Long trackRefId;

    @DatabaseField
    boolean evaluated;

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

    public boolean isOverrideTracks() {
        return overrideTracks;
    }

    public void setOverrideTracks(boolean overrideTracks) {
        this.overrideTracks = overrideTracks;
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

    public Long[] getSpeakerRefIds() {
        return speakerRefIds;
    }

    public void setSpeakerRefIds(Long[] speakerRefIds) {
        this.speakerRefIds = speakerRefIds;
    }

    public Long getTrackRefId() {
        return trackRefId;
    }

    public void setTrackRefId(Long trackRefId) {
        this.trackRefId = trackRefId;
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    public static Comparator<Session> SESSION_BY_DATE_COMPARATOR = new Comparator<Session>() {
        @Override
        public int compare(Session object1, Session object2) {
            return object1.getStart().compareTo(object2.getStart());
        }
    };

}
