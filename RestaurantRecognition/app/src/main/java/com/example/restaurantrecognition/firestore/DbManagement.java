package com.example.restaurantrecognition.firestore;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DbManagement extends Fragment {

    ArrayList<Restaurant> restaurantList = new ArrayList<>();

    public ArrayList<Restaurant> getAllRestaurants (){
        DbManagement.AsyncTaskFirestore myAsyncTasks = new DbManagement.AsyncTaskFirestore();
        myAsyncTasks.execute();

        return restaurantList;
    }

    public class AsyncTaskFirestore extends AsyncTask<Void, Void, ArrayList<Restaurant>> {
        @Override
        protected void onPreExecute() {
            Log.d("PreExecute: ", "Running");
        }

        boolean flag = true;
        @Override
        protected ArrayList<Restaurant> doInBackground(Void... voids) {
            ArrayList<Restaurant> restaurantList = new ArrayList<>();
            Log.d("doInBackground: ", "Running");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference docRef = db.collection("restaurants");

            docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Restaurant restaurant = new Restaurant(document.getId(),
                                    document.get("id").toString(),
                                    document.get("name").toString(),
                                    document.getGeoPoint("gps").getLatitude(),
                                    document.getGeoPoint("gps").getLongitude(),
                                    null);
                            restaurantList.add(restaurant);
                        }
                        Log.d("2.2. Size of array: ", String.valueOf(restaurantList.size()));

                    }
                    else
                    {
                        Log.d("Error", "Error getting documents.", task.getException());
                    }
                }
            });
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("doInBackground: ", "Finishing");
            Log.d("2.3. Size of array: ", String.valueOf(restaurantList.size()));

            return restaurantList;
        }
        SearchFromFolder searchFromFolder;

        @Override
        protected void onPostExecute(ArrayList<Restaurant> restaurants) {
            Log.d("PostExecute: ", "Running");



            Log.d("2.4. Size of array: ", String.valueOf(restaurants.size()));
        }
    }
}
