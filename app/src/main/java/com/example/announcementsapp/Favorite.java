package com.example.announcementsapp;

public class Favorite {

    private String id;
    private String userID;
    private Announcement announcement;
    public  Favorite(){}

    public Favorite(String id , String userID, Announcement announcement){
        this.id = id ;
        this.userID  = userID;
        this.announcement = announcement;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public String getId() {
        return id;
    }

    public String getUserID() {
        return userID;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
