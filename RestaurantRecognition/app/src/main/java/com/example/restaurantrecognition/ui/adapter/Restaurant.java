package com.example.restaurantrecognition.ui.adapter;

import java.io.Serializable;

public class Restaurant implements Serializable {
    int id;
    String image;
    String name,address;
    String rating;
    

    public Restaurant(int id, String image, String name, String address, String rating) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.address = address;
        this.rating = rating;
    }

    public int getId() { return id;}

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

}
