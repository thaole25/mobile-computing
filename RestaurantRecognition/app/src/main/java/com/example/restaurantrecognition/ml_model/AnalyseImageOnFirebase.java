package com.example.restaurantrecognition.ml_model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import com.example.restaurantrecognition.firestore.Prediction;
import com.example.restaurantrecognition.firestore.Restaurant;
import java.util.ArrayList;

public class AnalyseImageOnFirebase extends FragmentActivity {
    private final int IMG_SIZE = 224;
    private final int IMG_CHANNEL = 3;

    public float[][][][] imagePreProcessing(Bitmap image) {
        Bitmap bitmap = Bitmap.createScaledBitmap(image, IMG_SIZE, IMG_SIZE, false);
        int batchNum = 0;
        float[][][][] input = new float[1][IMG_SIZE][IMG_SIZE][IMG_CHANNEL];
        for (int x = 0; x < IMG_SIZE; x++) {
            for (int y = 0; y < IMG_SIZE; y++) {
                int pixel = bitmap.getPixel(x, y);
                input[batchNum][x][y][0] = (Color.red(pixel)) / 255.0f;
                input[batchNum][x][y][1] = (Color.green(pixel)) / 255.0f;
                input[batchNum][x][y][2] = (Color.blue(pixel)) / 255.0f;
//                Log.i("Pixel : ", String.format("number %d", Color.red(pixel)));
            }
        }

        return input;
    }

    public Prediction retrievePredictions(ArrayList<Restaurant> restaurantsList, int id, float maxProbability){
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

    public int getIdOfBestRestaurant(float[] probabilities){
        int bestId = 0;
        for (int id = 0; id < probabilities.length; id++) {
            if (probabilities[id] > probabilities[bestId]){
                bestId = id;
            }
        }
        return bestId;
    }
}

