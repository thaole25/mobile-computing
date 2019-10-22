package com.example.restaurantrecognition.firestore;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class GPSLocation {

    public static final int LESS_DISTANCE = 50;

    public Prediction getMoreSimilarRestaurant(ArrayList<Restaurant> allRestaurants, double latitude, double longitude){
        Log.d("Size all restaurants", String.valueOf(allRestaurants.size()));
        Log.d("GPS Longitude", String.valueOf(longitude));
        Log.d(" GPS Latitude", String.valueOf(latitude));

        // Fake distances next to Pronto Pizza UMELB:
        latitude =-37.797324;
        longitude =144.961406;

        double lowerDistance = Double.MAX_VALUE;
        double distance;
        Prediction closeRestaurant = new Prediction();
        for (Restaurant restaurant: allRestaurants) {
            distance = distanceInMeters(latitude, longitude, restaurant.getLatitude(), restaurant.getLongitude());

            Log.d("Distance", String.valueOf(distance));
            if (distance <= LESS_DISTANCE){
                Log.d("Close Restaurant", "Restaurant Name: " + restaurant.getName() + "Distance: " + distance);
                if (distance < lowerDistance){
                    lowerDistance = distance;
                    closeRestaurant.restaurant = restaurant;
                    closeRestaurant.distance = lowerDistance;
                }
            }
        }
        Log.d("Best Restaurant", "Restaurant Name: " + closeRestaurant.getRestaurant().getName() + " - Distance: " + lowerDistance);

        return closeRestaurant;
    }

    public double distanceInMeters(double latitudeUser, double longitudeUser, double restaurantLatitude, double restaurantLongitude) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(restaurantLatitude-latitudeUser);
        double dLng = Math.toRadians(restaurantLongitude-longitudeUser);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(latitudeUser)) * Math.cos(Math.toRadians(restaurantLatitude)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distanceMeters = (earthRadius * c);

        return distanceMeters;
    }
}
