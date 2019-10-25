package com.example.restaurantrecognition.firestore;

import java.util.ArrayList;

public class GPSLocation {

    public static final int LESS_DISTANCE = 30;

    public ArrayList<Prediction> getMoreSimilarRestaurant(ArrayList<Restaurant> allRestaurants, double latitude, double longitude) {
        double lowerDistance = Double.MAX_VALUE;
        double distance;
        ArrayList<Prediction> closeRestaurantList = new ArrayList<>();
        for (Restaurant restaurant : allRestaurants) {
            distance = distanceInMeters(latitude, longitude, restaurant.getLatitude(), restaurant.getLongitude());
            if (distance <= LESS_DISTANCE) {
                Prediction closeRestaurant = new Prediction();
                closeRestaurant.restaurant = restaurant;
                closeRestaurant.distance = lowerDistance;
                closeRestaurantList.add(closeRestaurant);
            }
        }
        return closeRestaurantList;
    }

    public double distanceInMeters(double latitudeUser, double longitudeUser, double restaurantLatitude, double restaurantLongitude) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(restaurantLatitude - latitudeUser);
        double dLng = Math.toRadians(restaurantLongitude - longitudeUser);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitudeUser)) * Math.cos(Math.toRadians(restaurantLatitude)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceMeters = (earthRadius * c);
        return distanceMeters;
    }
}
