package com.example.restaurantrecognition.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FirestoreDb {

    public void getRestaurants () throws ExecutionException, InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference restaurantRefList = db.collection("restaurants");
        ArrayList<Restaurant> restaurantList = new ArrayList<>();

        restaurantRefList.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("1.1. Starting for: ", String.valueOf(restaurantList.size()));

                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Restaurant restaurant = new Restaurant(document.getId(),
                            document.get("id").toString(),
                            document.get("name").toString(),
                            document.getGeoPoint("gps").getLatitude(),
                            document.getGeoPoint("gps").getLongitude(),
                            null);
                    restaurantList.add(restaurant);
                    }
                Log.d("1.2. After for: ", String.valueOf(restaurantList.size()));

            }
            })
            .addOnFailureListener(new OnFailureListener(){

                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Error", "Error getting documents.");

                }
            });
        Log.d("1.3. End method: ", String.valueOf(restaurantList.size()));

    }
}
