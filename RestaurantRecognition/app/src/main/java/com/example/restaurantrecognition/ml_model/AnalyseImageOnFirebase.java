package com.example.restaurantrecognition.ml_model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.firestore.DatabaseManagement;
import com.example.restaurantrecognition.firestore.Prediction;
import com.example.restaurantrecognition.firestore.Restaurant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import java.util.ArrayList;

import butterknife.BindView;

public class AnalyseImageOnFirebase {

    private FirebaseModelOutputs output;
    private DatabaseManagement dbManagement = new DatabaseManagement();
    private final int IMG_SIZE = 224;
    private final int IMG_CHANNEL = 3;
    private final int IMG_CLASSES = 11;
    private String finalOuput = "Cannot predict";

    private Prediction retrievePredictions(ArrayList<Restaurant> restaurantsList, int id, float maxProbability){
        for (Restaurant restaurant : restaurantsList){
            if (Integer.parseInt(restaurant.getId()) == id){
//                getPrediction = String.format("Id: %d, Name: %s, Prob: %1.4f", id, restaurant.getName(), maxProbability);
                Log.i("Results: ", String.format("Id: %d, Name: %s, Prob: %1.4f", id, restaurant.getName(), maxProbability));
                Prediction prediction = new Prediction(restaurant, maxProbability);
                return prediction;
            }
        }
        return null;
    }

    private int getIdOfBestRestaurant(float[] probabilities){
        int bestId = 0;
        for (int id = 0; id < probabilities.length; id++) {
            if (probabilities[id] > probabilities[bestId]){
                bestId = id;
            }
        }
        return bestId;
    }

    public String sendImagetoFirebase(Bitmap image) {
        FirebaseCustomLocalModel localModel;
        FirebaseModelInterpreterOptions options;
        FirebaseModelInputOutputOptions inputOutputOptions;

        localModel = new FirebaseCustomLocalModel.Builder().setAssetFilePath("restaurants-detector.tflite").build();

        FirebaseModelInterpreter interpreter;
        try {
            options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, IMG_SIZE, IMG_SIZE, IMG_CHANNEL})
                            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, IMG_CLASSES})
                            .build();

        float[][][][] input = imagePreProcessing(image);

        FirebaseModelInputs inputs = new FirebaseModelInputs.Builder().add(input).build();
        interpreter.run(inputs,inputOutputOptions).addOnSuccessListener(
                new OnSuccessListener<FirebaseModelOutputs>() {
                    @Override
                    public void onSuccess(FirebaseModelOutputs result) {
                        float[][] output = result.getOutput(0);
                        float[] probabilities = output[0];
//                        for (int i = 0; i < probabilities.length; i++){
//                            System.out.println(probabilities[i]);
//                        }
                        int bestId = getIdOfBestRestaurant(probabilities);

                        //Retrieve restaurants from firestore
                        ArrayList<Restaurant> restaurants = new ArrayList<>();
                        dbManagement.readData(new DatabaseManagement.FirestoreCallBack() {
                            @Override
                            public void onCallBack(ArrayList<Restaurant> restaurantArrayList) {
                                for (Restaurant restaurant: restaurantArrayList) {
                                    restaurants.add(restaurant);
                                }
                            }
                        });
                        Log.i("Best id: ", String.format("Id: %d,", bestId));

                        // Retrieve best prediction from restaurants
                        Prediction prediction = retrievePredictions(restaurants, bestId, probabilities[bestId]);
                        if (prediction != null){
                            finalOuput = String.format("Id: %d, Name: %s, Prob: %1.4f", prediction.getRestaurant().getId(), prediction.getRestaurant().getName(), prediction.getPrediction());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
        return finalOuput;
    }

    private float[][][][] imagePreProcessing(Bitmap image) {
        Bitmap bitmap = Bitmap.createScaledBitmap(image, IMG_SIZE, IMG_SIZE, true);
        int batchNum = 0;
        float[][][][] input = new float[1][IMG_SIZE][IMG_SIZE][IMG_CHANNEL];
        for (int x = 0; x < IMG_SIZE; x++) {
            for (int y = 0; y < IMG_SIZE; y++) {
                int pixel = bitmap.getPixel(x, y);
                input[batchNum][x][y][0] = (Color.red(pixel)) / 128.0f; /// 255;
                input[batchNum][x][y][1] = (Color.green(pixel)) / 128.0f;  // / 255;
                input[batchNum][x][y][2] = (Color.blue(pixel)) / 128.0f; /// / 255;
            }
        }
        return input;
    }
}

