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

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize


/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
@Parcelize
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
) : Parcelable

@Parcelize
data class SponsorshipPackage(
    var name: String? = null,
    var displayOrder: Int = 0
) : Parcelable

