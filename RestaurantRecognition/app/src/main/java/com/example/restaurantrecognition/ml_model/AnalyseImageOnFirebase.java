package com.example.restaurantrecognition.ml_model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.restaurantrecognition.firestore.Prediction;
import com.example.restaurantrecognition.firestore.Restaurant;
import com.google.android.gms.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;


public class AnalyseImageOnFirebase extends FragmentActivity {
    private final int IMG_SIZE = 224;
    private final int IMG_CHANNEL = 3;
    private final double PROBABILITY_THRESHOLD = 0.6;

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
            }
        }
        return input;
    }

    public ArrayList<Prediction> retrieveTopPredictions(ArrayList<Restaurant> restaurantsList, float[] probabilities, int topNumber) {
        ArrayList<Prediction> bestRestaurantsList = new ArrayList<>();
        float[] copyProbabititiles = Arrays.copyOf(probabilities, probabilities.length);
        ArrayList<Integer> restaurantIndices = new ArrayList<>();
        Arrays.sort(copyProbabititiles);
        int start = copyProbabititiles.length - 1;
        int end = copyProbabititiles.length - topNumber;
        for (int i = start; i >= end; i--) {
            for (int j = 0; j < probabilities.length; j++) {
                if (probabilities[j] > PROBABILITY_THRESHOLD && probabilities[j] == copyProbabititiles[i]) {
                    restaurantIndices.add(j);
                    break;
                }
            }
        }
        for (Integer resIndex : restaurantIndices) {
            for (Restaurant res : restaurantsList) {
                if (Integer.parseInt(res.getId()) == resIndex) {
                    Prediction prediction = new Prediction(res, probabilities[resIndex]);
                    bestRestaurantsList.add(prediction);
                }
            }
        }
        return bestRestaurantsList;
    }
}

