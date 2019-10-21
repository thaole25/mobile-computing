package com.example.restaurantrecognition.sqlite;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
public class SqliteManager {
    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public SqliteManager(Context c) {
        context = c;
    }

    public SqliteManager openDatabase() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void closeDatabase() {
        dbHelper.close();
    }

    public void insertRestaurant(String id, String name, String address, double latitude, double longitude, String photoUrl, String zomatoId) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ID, id);
        contentValue.put(DatabaseHelper.NAME, name);
        contentValue.put(DatabaseHelper.ADDRESS, address);
        contentValue.put(DatabaseHelper.LATITUDE, latitude);
        contentValue.put(DatabaseHelper.LONGITUDE, longitude);
        contentValue.put(DatabaseHelper.PHOTO_URL, photoUrl);
        contentValue.put(DatabaseHelper.ZOMATO_ID, zomatoId);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public void insertRestaurant(ContentValues contentValues) {
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetchRestaurants() {
        String[] columns = new String[] {
                DatabaseHelper.ID,
                DatabaseHelper.NAME,
                DatabaseHelper.ADDRESS,
                DatabaseHelper.LATITUDE,
                DatabaseHelper.LONGITUDE,
                DatabaseHelper.PHOTO_URL,
                DatabaseHelper.ZOMATO_ID
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }
}
