package com.example.restaurantrecognition.sqlite;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
public class DatabaseHelper  extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "RESTAURANT";

    // Table columns
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String ADDRESS = "ADDRESS";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String PHOTO_URL = "PHOTO_URL";
    public static final String ZOMATO_ID = "ZOMATO_ID";
    // Database Information
    static final String DB_NAME = "RestaurantFinder.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS RESTAURANT(ID VARCHAR,NAME VARCHAR, ADDRESS VARCHAR, LATITUDE REAL, LONGITUDE REAL, PHOTO_URL VARCHAR, ZOMATO_ID VARCHAR);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}