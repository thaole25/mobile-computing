package com.example.restaurantrecognition;

import android.util.Log;

import com.example.restaurantrecognition.ui.adapter.JSONAdapter;
import com.example.restaurantrecognition.ui.zomatoapi.ZomatoAccess;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZomatoAccessTest {
    ZomatoAccess zomatoAccess = new ZomatoAccess();

    @Test
    public void findLocationTest() {
        double lat = -37.7793;
        double lon = 144.9485;
        String address = "Parkville";

        System.out.println(zomatoAccess.findNearbyLocation(lat,lon,address));

    }

    @Test
    public void findRestaurantTest() {
        double lat = -37.7793;
        double lon = 144.9485;
        String restaurant = "Pronto Pizza";
        int cityId = 259;

        System.out.println(zomatoAccess.findMatchingRestaurants(restaurant, cityId, lat,lon));
    }

    @Test
    public void getReviewTest() {
        int res_id = 16578116;
        int offset = 0;

        System.out.println(zomatoAccess.getReview(res_id,offset));
    }

    @Test
    public void GetRestaurantDetailsTest() {
        int res_id = 16578116;

        System.out.println(zomatoAccess.getRestaurantDetails(res_id));
    }

}
