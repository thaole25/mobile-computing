package com.example.restaurantrecognition.ui.adapter;

public class Review {

    String id;
    String rating;
    String review_text;
    String review_time;
    String user_name;

    public Review(String id, String rating, String review_text, String review_time, String user_name) {

        this.id = id;
        this.rating = rating;
        this.review_text = review_text;
        this.review_time = review_time;
        this.user_name = user_name;

    }

    public String getId() {
        return id;
    }

    public String getRating() {
        return rating;
    }

    public String getReview_text() {
        return review_text;
    }

    public String getReview_time() {
        return review_time;
    }

    public String getUser_name() {
        return user_name;
    }

}
