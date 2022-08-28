package com.example.announcementsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{

    // Table Name
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ANNOUNCEMENT= "announcements";

    // Table users columns
    public static final String USER_ID = "id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String PASSWORD = "password";
    public static final String AGE = "userAge";
    public static final String USER_LOCATION = "userLocation";


    // Table announcements columns
    public static final String ANN_ID = "id";
    public static final String userID = "userID";
    public static final String img = "img";
    public static final String title = "title";
    public static final String price = "price";
    public static final String location = "location";
    public static final String description = "description";
    public static final String announcementDate = "announcementDate";

    // Database Information
    static final String DB_NAME = "ANNOUNCEMENT.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_USERS_TABLE = "create table " + TABLE_USERS + "("
            + USER_ID
            + " TEXT NOT NULL, " +
            NAME + " TEXT NOT NULL, " +
            EMAIL + " TEXT NOT NULL, " +
            PHONE + " TEXT NOT NULL, " +
            AGE + " TEXT NOT NULL, " +
            USER_LOCATION + " TEXT NOT NULL, " +
            PASSWORD + " TEXT NOT NULL);";

    private static final String CREATE_ANNOUNCE_TABLE = "create table " + TABLE_ANNOUNCEMENT + "("
            + ANN_ID
            + " TEXT NOT NULL, " +
            userID + " TEXT NOT NULL, " +
            title + " TEXT NOT NULL, " +
            price + " TEXT NOT NULL, " +
            location + " TEXT NOT NULL, " +
            description + " TEXT NOT NULL, " +
            img + " TEXT NOT NULL, " +
            announcementDate + " TEXT NOT NULL);";

    private  SQLiteDatabase database = null;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ANNOUNCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOUNCEMENT);
        onCreate(db);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }
}
