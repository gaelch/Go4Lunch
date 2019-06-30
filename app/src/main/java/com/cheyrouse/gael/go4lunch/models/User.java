package com.cheyrouse.gael.go4lunch.models;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class User implements Serializable {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private boolean notification;
    private String choice;
    private String eMail;

    public User() { }



    public User(String uid, String username, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
    }


    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public boolean isNotification() { return notification; }
    public String getChoice() { return choice; }
    public String geteMail() { return eMail;}

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setNotification(boolean notification) {this.notification = notification; }
    public void setChoice(String choice) {this.choice = choice; }
    public void seteMail(String eMail) { this.eMail = eMail;}
}
