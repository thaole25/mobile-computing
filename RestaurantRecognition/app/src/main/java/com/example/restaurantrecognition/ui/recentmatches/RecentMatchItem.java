package com.example.restaurantrecognition.ui.recentmatches;

public class RecentMatchItem {
    public static final String PREFERENCES_STORE_NAME = "recent-matches-store";

    public final String id;
    public final String restaurantName;
    public final String address;

    public RecentMatchItem(String id, String restaurantName, String address) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.address = address;
    }

    @Override
    public String toString() {
        return restaurantName;
    }
}
