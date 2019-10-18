package com.example.restaurantrecognition.ml_model;

import android.graphics.Bitmap;
import android.graphics.Color;

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
public class AnalyseImageOnFirebase {

    private FirebaseModelOutputs output;

    public int[] sendImagetoFirebase(Bitmap image) {
        FirebaseCustomLocalModel localModel;
        FirebaseModelInterpreterOptions options;
        FirebaseModelInputOutputOptions inputOutputOptions;
        output = null;

        localModel = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("restaurants-detector.tflite")
                .build();

        FirebaseModelInterpreter interpreter;
        try {
             options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
            inputOutputOptions =
                    new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 224, 224, 3})
                            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 11})
                            .build();

        float[][][][] input = imagePreProcessing(image);

        //interpret input so that it can be used by Firebase
            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                    .add(input)  // add() as many input arrays as your model requires
                    .build();
                    interpreter.run(inputs,inputOutputOptions).addOnSuccessListener(
                            new OnSuccessListener<FirebaseModelOutputs>() {
                                @Override
                                public void onSuccess(FirebaseModelOutputs result) {
                                    output = result.getOutput(0);
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
    return translateOutput(output);
    }

    //breaks down an image into rgb values for each pixel
    private float[][][][] imagePreProcessing(Bitmap image) {
        Bitmap bitmap = Bitmap.createScaledBitmap(image, 224, 224, true);
        int batchNum = 0;
        float[][][][] input = new float[1][224][224][3];
        for (int x = 0; x < 224; x++) {
            for (int y = 0; y < 224; y++) {
                int pixel = bitmap.getPixel(x, y);
                //check with Thao ------TO-DO
                input[batchNum][x][y][0] = (Color.red(pixel)) / 255;
                input[batchNum][x][y][1] = (Color.green(pixel)) / 255;
                input[batchNum][x][y][2] = (Color.blue(pixel)) / 255;
            }
        }
        return input;
    }

    //translates output function
    //check with Thao ------TO-DO
    private int [] translateOutput(FirebaseModelOutputs output){

        return null;
    }
}

