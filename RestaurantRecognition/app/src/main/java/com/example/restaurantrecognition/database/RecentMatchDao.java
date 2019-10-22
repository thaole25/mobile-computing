package com.example.restaurantrecognition.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecentMatchDao {
    @Query("SELECT * FROM recent_matches")
    List<RecentMatch> getAll();

    @Insert
    void insert(RecentMatch... recentMatches);

    @Delete
    void delete(RecentMatch recentMatch);
}
