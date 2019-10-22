package com.example.restaurantrecognition.ui.recentmatches;

public class RecentMatchItem {
    public final int id;
    public final String restaurantName;
    public final int zomatoId;

    public RecentMatchItem(int id, String restaurantName, int zomatoId) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.zomatoId = zomatoId;
    }

    @Override
    public String toString() {
        return restaurantName;
    }
}
