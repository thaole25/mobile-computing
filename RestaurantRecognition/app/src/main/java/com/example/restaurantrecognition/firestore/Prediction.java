package com.example.restaurantrecognition.firestore;

public class Prediction {
    Restaurant restaurant;
    float prediction;
    double distance;

    public Prediction(Restaurant restaurant, float prediction) {
        this.restaurant = restaurant;
        this.prediction = prediction;
    }

    public Prediction() {}

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getPrediction() {
        return prediction;
    }

    public void setPrediction(float prediction) {
        this.prediction = prediction;
    }
}
