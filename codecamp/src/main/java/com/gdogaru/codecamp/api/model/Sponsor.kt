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


/**
 * Created by Gabriel Dogaru (gdogaru@DatabaseField    @gmail.com)
 */
data class Sponsor(

        @JsonProperty("name")
        var name: String,

        @JsonProperty("websiteUrl")
        var websiteUrl: String? = null,

        @JsonProperty("logoUrl")
        var logoUrl: String? = null,

        @JsonProperty("sponsorshipPackage")
        var sponsorshipPackage: String? = null,

        @JsonProperty("displayOrder")
        var displayOrder: Int = 0
)

data class SponsorshipPackage(
        var name: String? = null,
        var displayOrder: Int = 0
)

