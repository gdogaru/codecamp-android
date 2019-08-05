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
import java.io.Serializable

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
data class Venue(
        @JsonProperty("Id")
        var id: Long = 0,

        @JsonProperty("name")
        var name: String,

        @JsonProperty("city")
        var city: String,

        @JsonProperty("latitude")
        var latitude: Double = 0.toDouble(),

        @JsonProperty("longitude")
        var longitude: Double = 0.toDouble(),

        @JsonProperty("country")
        var country: String,

        @JsonProperty("directions")
        var directions: String
) : Serializable
