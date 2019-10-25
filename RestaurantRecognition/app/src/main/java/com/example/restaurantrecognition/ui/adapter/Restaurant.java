package com.example.restaurantrecognition.ui.adapter;

import java.io.Serializable;

public class Restaurant implements Serializable {
    String id;
    String image;
    String name, address;
    String rating;
    String menuURL;
    double lat;
    double lon;
    String priceForTwo;
    String currency;

    public Restaurant(String id, String image, String name, String address, String rating,
                      String menuURL, double lat, double lon, String priceForTwo, String currency) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.menuURL = menuURL;
        this.lat = lat;
        this.lon = lon;
        this.priceForTwo = priceForTwo;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getRating() {
        return rating;
    }

    public String getMenuURL() {
        return menuURL;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getPriceForTwo() {
        return priceForTwo;
    }

    public String getCurrency() {
        return currency;
    }
}
