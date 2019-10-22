package com.example.restaurantrecognition.firestore;

public class Prediction {
    Restaurant restaurant;
    float score;
    double distance;

    public Prediction(Restaurant restaurant, float score) {
        this.restaurant = restaurant;
        this.score = score;
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

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
