package com.example.restaurantrecognition.ui.adapter;

public class Food {

    String dish_id;
    String name;
    String price;

    public Food(String dish_id, String name, String price) {
        this.dish_id = dish_id;
        this.name = name;
        this.price = price;
    }

    public String getDish_id() {
        return dish_id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
