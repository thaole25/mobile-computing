package com.example.restaurantrecognition.ui.adapter;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONAdapter {

    public int getLocationId(String str) {

        int city_id = 0;

        try {
            JSONObject completeJSON = new JSONObject(str);
            JSONArray jsonArray = completeJSON.getJSONArray("location_suggestions");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            city_id = jsonObject.getInt("city_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return city_id;

    }

    public List<Restaurant> getRestaurantList(String str) {

        ArrayList<Restaurant> restaurantList = new ArrayList<>();

        try {
            JSONObject completeJSON = new JSONObject(str);
            JSONArray jsonArray = completeJSON.getJSONArray("restaurants");
            JSONObject restaurantObject;

            for (int i=0; i<jsonArray.length(); i++) {
                restaurantObject = jsonArray.getJSONObject(i);
                JSONObject restaurantObjectVal = restaurantObject.getJSONObject("restaurant");
                JSONObject locationObject = restaurantObjectVal.getJSONObject("location");
                JSONObject ratingObject = restaurantObjectVal.getJSONObject("user_rating");
                restaurantList.add(new Restaurant(restaurantObjectVal.getInt("id"),restaurantObjectVal.getString("thumb"),restaurantObjectVal.getString("name"),locationObject.getString("address"),ratingObject.getString("aggregate_rating")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return restaurantList;

    }

}
