package com.example.restaurantrecognition.ui.recentmatches;

public class RecentMatchItem {
    public static final String PREFERENCES_STORE_NAME = "recent-matches-store";

    public final String id;
    public final String restaurantName;
    public final String address;
    public final double latitude;
    public final double longitude;
    public final int zomatoId;

    public RecentMatchItem(String id, String restaurantName, String address, double latitude, double longitude, int zomatoId) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zomatoId = zomatoId;
    }

    @Override
    public String toString() {
        return restaurantName;
    }
}
