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

package com.gdogaru.codecamp.api.model

import org.threeten.bp.LocalDateTime
import java.io.Serializable
import java.util.*

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
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
