package com.example.restaurantrecognition.firestore;

import android.graphics.Bitmap;

public class Restaurant {
    String documentId;
    String id;
    String name;
    double latitude;
    double longitude;
    String address;
    Bitmap photo;

    public Restaurant() { }

    public Restaurant(String documentId, String id, String name, double latitude, double longitude, String address, Bitmap photo) {
        this.documentId = documentId;
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
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

    public String getAddress() {
        return address;
    }
}
