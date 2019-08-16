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

package com.gdogaru.codecamp.api.model


import com.fasterxml.jackson.annotation.JsonProperty
import com.gdogaru.codecamp.util.DateUtil
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.util.*

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

data class Schedule(
        var date: LocalDateTime,
        var timeSlots: List<TimeSlot>,
        var tracks: List<Track>,
        var sessions: List<Session>
)


data class TimeSlot(
        var startTime: LocalTime?,
        var endTime: LocalTime?
)

data class Track(
        @JsonProperty("name")
        var name: String,

        @JsonProperty("capacity")
        var capacity: String,

        @JsonProperty("description")
        var description: String,

        @JsonProperty("displayOrder")
        var displayOrder: Int = 0
) {
    fun getFullDescription() = String.format(Locale.getDefault(), "%s, %s seats, %s", name, capacity, description)
}


data class Session(
        @JsonProperty("title")
        var title: String,

        @JsonProperty("description")
        var description: String?,

        @JsonProperty("startTime")
        var startTime: LocalTime?,

        @JsonProperty("endTime")

        var endTime: LocalTime?,
        @JsonProperty("speakers")
        var speakerIds: List<String>?,

        @JsonProperty("speakingLang")
        var speakingLang: String?,

        @JsonProperty("level")
        var level: String?,

        @JsonProperty("allTracks")
        var allTracks: Boolean? = null,

        @JsonProperty("track")
        var track: String?

) {
    val id: String
        get() = startTime.toString() + track

    val formattedInterval: String
        get() = DateUtil.formatPeriod(startTime, endTime)
}
