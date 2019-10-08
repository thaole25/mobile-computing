package com.example.restaurantrecognition.ui.zomatoapi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ZomatoAccess {
    final String BASE_URL = "https://developers.zomato.com/api/v2.1/";
    final String API_KEY = "8c328124fafa61a736ac58b6c026f012";

    // Getting a nearby location
    public String findNearbyLocation(double lat, double lon, String address) {
        final String method_path = "locations?";

        URL url = null;
        HttpURLConnection conn = null;

        String textResult = "";
        String urlString="";
        final int countParameter = 1;

        try {
            // Define Basic String URL
            urlString+=BASE_URL;
            urlString+=method_path;

            // Add Request Body to URL;
            urlString+="query="; urlString+=address;
            urlString+="&lat="; urlString+=lat;
            urlString+="&lon="; urlString+=lon;
            urlString+="&count="; urlString+=countParameter;

            // Define URL
            url = new URL(urlString);

            // Open connection
            conn = (HttpURLConnection) url.openConnection();

            // Set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            // Set connection method to GET
            conn.setRequestMethod("GET");

            // Add HTTP Headers
            conn.setRequestProperty("user-key",API_KEY);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Accept","application/json");

            // Read the response
            Scanner inStream = new Scanner(conn.getInputStream());

            // Read the input stream and store it as string
            while(inStream.hasNextLine()) {
                textResult+=inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return textResult;
    }


    // Getting a list of matching restaurants
    public String findMatchingRestaurants(String restaurant, int cityId, double lat, double lon) {
        final String method_path = "search?";

        URL url = null;
        HttpURLConnection conn = null;

        String textResult = "";
        String urlString="";
        final int countParameter = 5;
        final int radiusArea = 100; // in Meter

        try {
            // Define Basic String URL
            urlString+=BASE_URL;
            urlString+=method_path;

            String restaurantBuffer = restaurant.replaceAll(" ","%20");

            // Add Request Body to URL;
            urlString+="entity_id="; urlString+=cityId;
            urlString+="&entity_type="; urlString+="city";
            urlString+="&q="; urlString+=restaurantBuffer;
            urlString+="&lat="; urlString+=lat;
            urlString+="&lon="; urlString+=lon;
            urlString+="&count="; urlString+=countParameter;
            urlString+="&radius="; urlString+=radiusArea;

            // Define URL
            url = new URL(urlString);

            // Open connection
            conn = (HttpURLConnection) url.openConnection();

            // Set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            // Set connection method to GET
            conn.setRequestMethod("GET");

            // Add HTTP Headers
            conn.setRequestProperty("user-key",API_KEY);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Accept","application/json");

            // Read the response
            Scanner inStream = new Scanner(conn.getInputStream());

            // Read the input stream and store it as string
            while(inStream.hasNextLine()) {
                textResult+=inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return textResult;

    }

    // Getting a list of restaurant reviews
    public String getReview(int res_id, int offset) {
        final String method_path = "reviews?";

        URL url = null;
        HttpURLConnection conn = null;

        String textResult = "";
        String urlString="";
        final int countParameter = 5;

        try {
            // Define Basic String URL
            urlString+=BASE_URL;
            urlString+=method_path;

            // Add Request Body to URL;
            urlString+="res_id="; urlString+=res_id;
            urlString+="&start="; urlString+=offset;
            urlString+="&count="; urlString+=countParameter;

            // Define URL
            url = new URL(urlString);

            // Open connection
            conn = (HttpURLConnection) url.openConnection();

            // Set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            // Set connection method to GET
            conn.setRequestMethod("GET");

            // Add HTTP Headers
            conn.setRequestProperty("user-key",API_KEY);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Accept","application/json");

            // Read the response
            Scanner inStream = new Scanner(conn.getInputStream());

            // Read the input stream and store it as string
            while(inStream.hasNextLine()) {
                textResult+=inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return textResult;
    }

    // We might not need this as getRestaurant already include the restaurant details
    public String getRestaurantDetails(int res_id) {
        final String method_path = "restaurant?";

        URL url = null;
        HttpURLConnection conn = null;

        String textResult = "";
        String urlString="";

        try {
            // Define Basic String URL
            urlString+=BASE_URL;
            urlString+=method_path;

            // Add Request Body to URL;
            urlString+="res_id="; urlString+=res_id;

            // Define URL
            url = new URL(urlString);

            // Open connection
            conn = (HttpURLConnection) url.openConnection();

            // Set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            // Set connection method to GET
            conn.setRequestMethod("GET");

            // Add HTTP Headers
            conn.setRequestProperty("user-key",API_KEY);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Accept","application/json");

            // Read the response
            Scanner inStream = new Scanner(conn.getInputStream());

            // Read the input stream and store it as string
            while(inStream.hasNextLine()) {
                textResult+=inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return textResult;
    }
}
