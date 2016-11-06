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

import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Gabriel Dogaru (gdogaru@DatabaseField    @gmail.com)
 */
public class Session implements Serializable {

    public static final Comparator<Session> SESSION_BY_DATE_COMPARATOR = new Comparator<Session>() {
        @Override
        public int compare(Session object1, Session object2) {
            return object1.getStartTime().compareTo(object2.getStartTime());
        }
    };
    @SerializedName("title")
    String title;
    @SerializedName("description")
    String description;
    @SerializedName("startTime")
    LocalTime startTime;
    @SerializedName("endTime")
    LocalTime endTime;
    @SerializedName("speakers")
    List<String> speakerIds;
    @SerializedName("speakingLang")
    String speakingLang;
    @SerializedName("level")
    String level;
    @SerializedName("allTracks")
    Boolean allTracks;
    @SerializedName("track")
    String track;


    public String getId() {
        return String.valueOf(startTime) + String.valueOf(track);
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public List<String> getSpeakerIds() {
        return speakerIds;
    }

    public void setSpeakerIds(List<String> speakerIds) {
        this.speakerIds = speakerIds;
    }

    public String getSpeakingLang() {
        return speakingLang;
    }

    public void setSpeakingLang(String speakingLang) {
        this.speakingLang = speakingLang;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }


    public Boolean getAllTracks() {
        return allTracks;
    }

    public void setAllTracks(Boolean allTracks) {
        this.allTracks = allTracks;
    }
}
