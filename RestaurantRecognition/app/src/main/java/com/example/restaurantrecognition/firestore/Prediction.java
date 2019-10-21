package com.example.restaurantrecognition.firestore;

public class Prediction {
    Restaurant restaurant;
    float prediction;

    public Prediction(Restaurant restaurant, float prediction) {
        this.restaurant = restaurant;
        this.prediction = prediction;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public float getPrediction() {
        return prediction;
    }

    public void setPrediction(float prediction) {
        this.prediction = prediction;
    }
}
