package com.example.restaurantrecognition.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RecentMatch {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "restaurant_name")
    public String restaurantName;

    @ColumnInfo(name = "zomato_id")
    public int zomatoId;
}
