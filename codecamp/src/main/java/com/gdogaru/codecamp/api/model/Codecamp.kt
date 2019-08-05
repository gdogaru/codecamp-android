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

import org.threeten.bp.LocalDateTime
import java.io.Serializable
import java.util.*

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
class Codecamp : EventSummary(), Serializable {

    var sponsorshipPackages: List<SponsorshipPackage>? = null
    var sponsors: List<Sponsor>? = null
    var schedules: List<Schedule>? = null
    var speakers: List<Speaker>? = null

}

class EventList : ArrayList<EventSummary>()

open class EventSummary {
    var refId: Long = 0
    var title: String? = null
    var startDate: LocalDateTime? = null
    var endDate: LocalDateTime? = null
    var venue: Venue? = null
}
