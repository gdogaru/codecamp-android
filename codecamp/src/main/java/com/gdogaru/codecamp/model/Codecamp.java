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

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class Codecamp extends EventSummary implements Serializable {

    private List<SponsorshipPackage> sponsorshipPackages;
    private List<Sponsor> sponsors;
    private List<Schedule> schedules;
    private List<Speaker> speakers;

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    public List<SponsorshipPackage> getSponsorshipPackages() {
        return sponsorshipPackages;
    }

    public void setSponsorshipPackages(List<SponsorshipPackage> sponsorshipPackages) {
        this.sponsorshipPackages = sponsorshipPackages;
    }

    public List<Sponsor> getSponsors() {
        return sponsors;
    }

    public void setSponsors(List<Sponsor> sponsors) {
        this.sponsors = sponsors;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

}
