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

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class Feedback {
    private String name;
    private String email;
    private String company;
    private String technology;
    private int shareMyContact; //0 share, 1 don't sare, 2 i don't care
    private int experience; // int in ani  - daca e cu select din interval ca pe site se ia nr max si se trimite, sau putem trece exact anii  pe mobile, cum vreti
    private long sessionID; // int id-ul sesiunii - cum e luat din celalalt json
    private String sessionName;//string[150] - numele sesiunii cum e in json
    private int feedbackValue; // int cu valori de la 1 la 5
    private String feedbackComment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public int getShareMyContact() {
        return shareMyContact;
    }

    public void setShareMyContact(int shareMyContact) {
        this.shareMyContact = shareMyContact;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public int getFeedbackValue() {
        return feedbackValue;
    }

    public void setFeedbackValue(int feedbackValue) {
        this.feedbackValue = feedbackValue;
    }

    public String getFeedbackComment() {
        return feedbackComment;
    }

    public void setFeedbackComment(String feedbackComment) {
        this.feedbackComment = feedbackComment;
    }
}
