package com.example.announcementsapp;

public class User{
    private String userID;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userAge;
    private String userLocation;

    public User(){}
    public User(String userID, String userName, String userEmail, String userPhone, String userAge, String userLocation)
    {
        this.userID = userID;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userAge = userAge;
        this.userLocation = userLocation;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserAge() {
        return userAge;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public String getUserPhone() {
        return userPhone;
    }
}
