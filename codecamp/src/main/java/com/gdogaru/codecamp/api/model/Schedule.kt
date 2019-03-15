package com.gdogaru.codecamp.api.model


import com.fasterxml.jackson.annotation.JsonProperty
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

/**
 * Created by Gabriel on 10/2/2016.
 */

data class Schedule(
        var date: LocalDateTime,
        var timeSlots: List<TimeSlot>,
        var tracks: List<Track>,
        var sessions: List<Session>
)


data class TimeSlot(
        var startTime: LocalTime,
        var endTime: LocalTime
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
)


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

    companion object {

        val SESSION_BY_DATE_COMPARATOR: java.util.Comparator<Session> = Comparator { object1, object2 ->
            object1.startTime?.compareTo(object2.startTime) ?: -1
        }
    }
}
