package com.example.restaurantrecognition.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {RecentMatch.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecentMatchDao recentMatchDao();

    private static AppDatabase appDatabase = null;

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, "app-database").build();
        }
        return appDatabase;
    }
}
