package com.example.restaurantrecognition.ui.recentmatches;

public class RecentMatchItem {
    public final String id;
    public final String restaurantName;

    public RecentMatchItem(String id, String restaurantName) {
        this.id = id;
        this.restaurantName = restaurantName;
    }

    @Override
    public String toString() {
        return restaurantName;
    }
}
