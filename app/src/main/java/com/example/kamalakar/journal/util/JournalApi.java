package com.example.kamalakar.journal.util;

import android.app.Application;

public class JournalApi extends Application {
    String username;
    String userId;
    public static JournalApi INSTANCE;

    public JournalApi() {
    }

    public static JournalApi getInstance(){
        if(INSTANCE==null)
            INSTANCE=new JournalApi();
        return INSTANCE;
    }

    public JournalApi(String username, String userId) {
        this.username = username;
        this.userId = userId;
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
}
