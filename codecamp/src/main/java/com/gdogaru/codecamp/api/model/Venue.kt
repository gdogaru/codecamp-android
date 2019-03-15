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

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
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
