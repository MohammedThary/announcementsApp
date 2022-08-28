package com.example.announcementsapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBManager {


    private DBHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public boolean addAnnouncement(Announcement an) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.ANN_ID, an.getId());
        contentValue.put(DBHelper.announcementDate, an.getAnnouncementDate());
        contentValue.put(DBHelper.description, an.getDescription());
        contentValue.put(DBHelper.location, an.getLocation());
        contentValue.put(DBHelper.price, an.getPrice());
        contentValue.put(DBHelper.title, an.getTitle());
        contentValue.put(DBHelper.userID, an.getUserID());
        contentValue.put(DBHelper.img, an.getImg());
        int d = (int)database.insert(DBHelper.TABLE_ANNOUNCEMENT, null, contentValue);
        return (d>0);
    }

    public boolean register(User user, String password) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.USER_ID, user.getUserID());
        contentValue.put(DBHelper.NAME, user.getUserName());
        contentValue.put(DBHelper.EMAIL, user.getUserEmail());
        contentValue.put(DBHelper.PHONE, user.getUserPhone());
        contentValue.put(DBHelper.PASSWORD, password);
        contentValue.put(DBHelper.USER_LOCATION, user.getUserLocation());
        contentValue.put(DBHelper.AGE, user.getUserAge());
        int d = (int)database.insert(DBHelper.TABLE_USERS, null, contentValue);
        return (d>0);
    }

    public boolean update(User user, String password) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.NAME, user.getUserName());
        contentValue.put(DBHelper.EMAIL, user.getUserEmail());
        contentValue.put(DBHelper.PHONE, user.getUserPhone());
        contentValue.put(DBHelper.PASSWORD, password);
        contentValue.put(DBHelper.USER_LOCATION, user.getUserLocation());
        contentValue.put(DBHelper.AGE, user.getUserAge());
        int d = (int)database.update(DBHelper.TABLE_USERS, contentValue, "id = ?", new String[]{user.getUserID()});
        return (d>0);
    }

    public User login(String email, String password) {
        ArrayList<String> array_list = new ArrayList<String>();
        String sql = "select * from "+DBHelper.TABLE_USERS+" where "+
                DBHelper.EMAIL + " = '"+email+"' and " +
                DBHelper.PASSWORD + " = '"+password+"'  ";
        Cursor res = database.rawQuery( sql, null );
        res.moveToFirst();
        User user = null;
        while(res.isAfterLast() == false) {
            @SuppressLint("Range") String id = res.getString(res.getColumnIndex(DBHelper.USER_ID));
            @SuppressLint("Range") String name = res.getString(res.getColumnIndex(DBHelper.NAME));
            @SuppressLint("Range") String em = res.getString(res.getColumnIndex(DBHelper.EMAIL));
            @SuppressLint("Range") String phone = res.getString(res.getColumnIndex(DBHelper.PHONE));
            @SuppressLint("Range") String location = res.getString(res.getColumnIndex(DBHelper.USER_LOCATION));
            @SuppressLint("Range") String age = res.getString(res.getColumnIndex(DBHelper.AGE));
            user = new User(id, name, em, phone, age, location);
            res.moveToNext();
        }
        return user;
    }

    public ArrayList<Announcement> getAnnouncements(String userID) {
        ArrayList  array_list = new ArrayList<Announcement>();
        String sql = "select * from "+DBHelper.TABLE_ANNOUNCEMENT+" where "+
                DBHelper.userID + " = '"+userID+"'  " ;
        Cursor res = database.rawQuery( sql, null );
        res.moveToFirst();
        Announcement ann = null;
        while(res.isAfterLast() == false) {
            @SuppressLint("Range") String id = res.getString(res.getColumnIndex(DBHelper.ANN_ID));
            @SuppressLint("Range") String title = res.getString(res.getColumnIndex(DBHelper.title));
            @SuppressLint("Range") String price = res.getString(res.getColumnIndex(DBHelper.price));
            @SuppressLint("Range") String location = res.getString(res.getColumnIndex(DBHelper.location));
            @SuppressLint("Range") String description = res.getString(res.getColumnIndex(DBHelper.description));
            @SuppressLint("Range") String announcementDate = res.getString(res.getColumnIndex(DBHelper.announcementDate));
            @SuppressLint("Range") String img = res.getString(res.getColumnIndex(DBHelper.img));
            ann = new Announcement(id, userID, title,  description, price, location, img);
            ann.setDate(announcementDate);
            array_list.add(ann);
            res.moveToNext();
        }
        return array_list;
    }

}
