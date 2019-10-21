package com.example.restaurantrecognition.firestore;

public class Prediction {
    Restaurant restaurant;
    float prediction;

    public Prediction(Restaurant restaurant, float prediction) {
        this.restaurant = restaurant;
        this.prediction = prediction;
    }
}
