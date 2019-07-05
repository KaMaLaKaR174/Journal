package com.example.kamalakar.journal.model;

import com.google.firebase.Timestamp;

public class Journal {
    String username;
    String userId;
    String title;
    String thoughts;
    String imageurl;
    Timestamp timestamp;

    public Journal() {
    }

    public Journal(String username, String userId, String title, String thoughts, String imageurl, Timestamp timestamp) {
        this.username = username;
        this.userId = userId;
        this.title = title;
        this.thoughts = thoughts;
        this.imageurl = imageurl;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
