package com.example.announcementsapp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Announcement {
    private String id;
    private String userID;
    private String title;
    private String price;
    private String description;
    private String announcementDate;
    private String location;
    private String img;


    public Announcement(){}
    public Announcement(String id, String userID, String title, String description ,String price , String location , String img)
    {
        this.id = id;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.img = img;
        this.announcementDate = this.getDate();
    }

    public String getImg() {
        return img;
    }

    public void setDate(String date){
        this.announcementDate = date;
    }
    private String getDate(){

        Date date = new Date();
        Date newDate = new Date(date.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        String stringdate = dt.format(newDate);
        return stringdate ;

    }
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUserID() {
        return userID;
    }

    public String getDescription() {
        return description;
    }

    public String getAnnouncementDate() {
        return announcementDate;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }
}
