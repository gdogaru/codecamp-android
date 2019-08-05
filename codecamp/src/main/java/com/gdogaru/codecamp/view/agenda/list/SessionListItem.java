/*
 * Copyright (c) 2019 Gabriel Dogaru - gdogaru@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.gdogaru.codecamp.view.agenda.list;

import org.threeten.bp.LocalTime;

import java.util.Comparator;
import java.util.List;

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
public class SessionListItem {

    public static Comparator<SessionListItem> SESSION_BY_DATE_COMPARATOR = new Comparator<SessionListItem>() {

        @Override
        public int compare(SessionListItem object1, SessionListItem object2) {
            return object1.getStart() == null ? -1 : object1.getStart().compareTo(object2.getStart());
        }
    };
    private String id;
    private String name;
    private LocalTime start;
    private LocalTime end;
    private String trackName;
    private List<String> speakerNames;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
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

}
