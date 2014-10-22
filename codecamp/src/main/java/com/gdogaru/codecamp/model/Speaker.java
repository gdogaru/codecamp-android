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
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Gabriel Dogaru (gdogaru@DatabaseField    @gmail.com)
 */
@DatabaseTable(tableName = "speakers")
public class Speaker {

    @DatabaseField(id = true)
    @SerializedName("Id")
    long id;
    @DatabaseField
    @SerializedName("FirstName")
    String firstName;
    @DatabaseField
    @SerializedName("LastName")
    String lastName;
    @DatabaseField
    @SerializedName("Title")
    String title;
    @DatabaseField
    @SerializedName("Bio")
    String bio;
    @DatabaseField
    @SerializedName("PortraitImageUrl")
    String portraitImageUrl;
    @DatabaseField
    @SerializedName("BlogUrl")
    String blogUrl;
    @DatabaseField
    @SerializedName("LinkedInProfileUrl")
    String linkedInProfileUrl;
    @DatabaseField
    @SerializedName("TwitterProfileUrl")
    String twitterProfileUrl;
    @DatabaseField
    @SerializedName("FacebookProfileUrl")
    String facebookProfileUrl;
    @DatabaseField
    @SerializedName("CompanyName")
    String companyName;
    @DatabaseField
    @SerializedName("CompanyWebsiteUrl")
    String companyWebsiteUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPortraitImageUrl() {
        return portraitImageUrl;
    }

    public void setPortraitImageUrl(String portraitImageUrl) {
        this.portraitImageUrl = portraitImageUrl;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public String getLinkedInProfileUrl() {
        return linkedInProfileUrl;
    }

    public void setLinkedInProfileUrl(String linkedInProfileUrl) {
        this.linkedInProfileUrl = linkedInProfileUrl;
    }

    public String getTwitterProfileUrl() {
        return twitterProfileUrl;
    }

    public void setTwitterProfileUrl(String twitterProfileUrl) {
        this.twitterProfileUrl = twitterProfileUrl;
    }

    public String getFacebookProfileUrl() {
        return facebookProfileUrl;
    }

    public void setFacebookProfileUrl(String facebookProfileUrl) {
        this.facebookProfileUrl = facebookProfileUrl;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyWebsiteUrl() {
        return companyWebsiteUrl;
    }

    public void setCompanyWebsiteUrl(String companyWebsiteUrl) {
        this.companyWebsiteUrl = companyWebsiteUrl;
    }
}
