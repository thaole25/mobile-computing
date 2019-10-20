package com.example.restaurantrecognition.ml_model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AnalyseImageOnFirebase {

    private FirebaseModelOutputs output;
    private final int IMG_SIZE = 224;
    private final int IMG_CHANNEL = 3;
    private final int IMG_CLASSES = 11;

    public void sendImagetoFirebase(Bitmap image) {
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
//                        BufferedReader reader = new BufferedReader(new InputStreamReader());
//                        for (int i = 0; i < probabilities.length; i++) {
//                            String label = reader.readLine();
//                            Log.i("MLKit", String.format("%s: %1.4f", label, probabilities[i]));
//                            Log.i("My results", String.format("%1.4f", probabilities[i]));
//                            System.out.println(probabilities[i]);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
    }

    private float[][][][] imagePreProcessing(Bitmap image) {
        Bitmap bitmap = Bitmap.createScaledBitmap(image, IMG_SIZE, IMG_SIZE, true);
        int batchNum = 0;
        float[][][][] input = new float[1][IMG_SIZE][IMG_SIZE][IMG_CHANNEL];
        for (int x = 0; x < IMG_SIZE; x++) {
            for (int y = 0; y < IMG_SIZE; y++) {
                int pixel = bitmap.getPixel(x, y);
                input[batchNum][x][y][0] = (Color.red(pixel)) / 255;
                input[batchNum][x][y][1] = (Color.green(pixel)) / 255;
                input[batchNum][x][y][2] = (Color.blue(pixel)) / 255;
            }
        }
        return input;
    }
}

