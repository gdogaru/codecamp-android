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

/**
 * Created by Gabriel Dogaru (gdogaru@DatabaseField    @gmail.com)
 */
public class Sponsor {

    @SerializedName("name")
    String name;

    @SerializedName("websiteUrl")
    String websiteUrl;

    @SerializedName("logoUrl")
    String logoUrl;

    @SerializedName("sponsorshipPackage")
    String sponsorshipPackage;

    @SerializedName("displayOrder")
    int displayOrder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getSponsorshipPackage() {
        return sponsorshipPackage;
    }

    public void setSponsorshipPackage(String sponsorshipPackage) {
        this.sponsorshipPackage = sponsorshipPackage;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Sponsor{");
        sb.append("name='").append(name).append('\'');
        sb.append(", websiteUrl='").append(websiteUrl).append('\'');
        sb.append(", logoUrl='").append(logoUrl).append('\'');
        sb.append(", sponsorshipPackage='").append(sponsorshipPackage).append('\'');
        sb.append(", displayOrder=").append(displayOrder);
        sb.append('}');
        return sb.toString();
    }
}
