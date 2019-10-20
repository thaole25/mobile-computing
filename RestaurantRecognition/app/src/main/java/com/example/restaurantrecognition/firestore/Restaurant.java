package com.example.restaurantrecognition.firestore;

import android.graphics.Bitmap;

public class Restaurant {
    String documentId;
    String id;
    String name;
    double latitude;
    double longitude;
    Bitmap photo;

    public Restaurant(String documentId, String id, String name, double latitude, double longitude, Bitmap photo) {
        this.documentId = documentId;
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
