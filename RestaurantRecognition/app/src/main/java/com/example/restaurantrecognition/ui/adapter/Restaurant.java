package com.example.restaurantrecognition.ui.adapter;

import java.io.Serializable;

public class Restaurant implements Serializable {
    String id;
    String image;
    String name,address;
    String rating;
    String menuURL;
    double lat;
    double lon;
    

    public Restaurant(String id, String image, String name, String address, String rating,
                      String menuURL, double lat, double lon) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.menuURL = menuURL;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() { return id;}

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

    public String getMenuURL() { return menuURL;}

    public double getLat() { return lat; }

    public double getLon() { return lon; }

}
