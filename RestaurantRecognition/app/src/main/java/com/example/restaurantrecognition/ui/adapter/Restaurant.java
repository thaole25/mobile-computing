package com.example.restaurantrecognition.ui.adapter;

public class Restaurant {
    int image;
    String name,address;
    String rating;

    public Restaurant(int image, String name, String address, String rating) {
        this.image = image;
        this.name = name;
        this.address = address;
        this.rating = rating;
    }

    public int getImage() {
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

}
