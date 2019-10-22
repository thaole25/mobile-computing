package com.example.restaurantrecognition.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DatabaseManagement {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference restaurantRefList = db.collection("restaurants");

    // Read the document: restaurants from firestore and save them in a arraylist.
    public ArrayList<Restaurant> readData(FirestoreCallBack fireStoreCallback){
        ArrayList<Restaurant> restaurantList = new ArrayList<>();
        restaurantRefList.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Restaurant restaurant = new Restaurant(document.getId(),
                                document.get("id").toString(),
                                document.get("name").toString(),
                                document.getGeoPoint("gps").getLatitude(),
                                document.getGeoPoint("gps").getLongitude(),
                                document.get("address").toString(),
                                null);
                        restaurantList.add(restaurant);
                    }
                    fireStoreCallback.onCallBack(restaurantList);
                }
                else
                {
                    Log.d("Error", "Error getting documents.", task.getException());
                }
            }
        });
        return restaurantList;
    }

    // Firestore works asynchronously, so it does not retrieve the data correctly and return [].
    // Therefore I found this solution is necessary using a interface callback
    public interface FirestoreCallBack{
        void onCallBack(ArrayList<Restaurant> restaurantArrayList);
    }

    // Example to retrieve data , use this code wherever you want to get the data
    public ArrayList<Restaurant> getAllData() {

        //DatabaseManagement db = new DatabaseManagement()
        //db.readData(restaurantArrayList -> Log.d("Size of array: ", String.valueOf(restaurantArrayList.size())));

        ArrayList<Restaurant> restaurants = new ArrayList<>();
        readData(new FirestoreCallBack() {
            @Override
            public void onCallBack(ArrayList<Restaurant> restaurantArrayList) {
                Log.d("Result", String.valueOf(restaurantArrayList.size()));
            }
        });

        return restaurants;
    }
}
