package com.example.restaurantrecognition.ui.search;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.firestore.DatabaseManagement;
import com.example.restaurantrecognition.firestore.Prediction;
import com.example.restaurantrecognition.firestore.Restaurant;
import com.example.restaurantrecognition.ml_model.AnalyseImageOnFirebase;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getExternalFilesDirs;
import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.FragmentInteractionListener;
import com.example.restaurantrecognition.ui.recentmatches.RecentMatchesFragment;
import com.example.restaurantrecognition.ui.searchresult.SearchResultFragment;

public class SearchFragment extends Fragment {

    private final int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private final int IMG_SIZE = 224;
    private final int IMG_CHANNEL = 3;
    private final int IMG_CLASSES = 11;

//    private TextView txtResult;
    private AnalyseImageOnFirebase aiModel = new AnalyseImageOnFirebase();
    private final int REQUEST_CODE_GET_IMAGE = 25;

    private FirebaseModelOutputs output;
    private DatabaseManagement dbManagement = new DatabaseManagement();

    private String finalOuput = "Cannot predict";

//    @BindView(R.id.btnSearchImage)
//    Button buttonSearchImage;

    @BindView(R.id.view_finder)
    TextureView textureView;

    @BindView(R.id.imgCapture)
    ImageButton imgBtn;

    private FragmentInteractionListener mListener;
    @BindView(R.id.btnChooseFromFolder)
    Button btnSelectFromFolder;

    @BindView(R.id.text_prediction)
    TextView txtResult;

    private SearchViewModel searchViewModel;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mListener = (FragmentInteractionListener)getActivity();
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        View searchView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, searchView);

        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        return searchView;
    }

    private void startCamera() {
        CameraX.unbindAll();
        Rational aspectRatio = new Rational(textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight());

        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio)
                .setTargetResolution(screen).build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                output -> {
                    ViewGroup parent = (ViewGroup) textureView.getParent();
                    parent.removeView(textureView);
                    parent.addView(textureView, 0);
                    textureView.setSurfaceTexture(output.getSurfaceTexture());
                    updateTransform();
                }
        );

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);

        imgBtn.setOnClickListener(v -> {
           File file = new File(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".jpg");
           imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
               @Override
               public void onImageSaved(@NonNull File file) {
                   Toast.makeText(getContext(), "Photo saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                   FirebaseStorage storage = FirebaseStorage.getInstance();
                   StorageReference storageRef = storage.getReference().child("uploads");
                   StorageMetadata metadata = new StorageMetadata.Builder()
                           .setContentType("image/jpg")
                           .build();
                   File compressedFile = null;
                   try {
                       compressedFile = new Compressor(getContext()).compressToFile(file);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   Uri filePath = null;
                   if (compressedFile == null)
                       filePath = Uri.fromFile(file);
                   else
                       filePath = Uri.fromFile(compressedFile);
                   StorageReference imageRef = storageRef.child(filePath.getLastPathSegment());
                   UploadTask uploadTask = imageRef.putFile(filePath, metadata);
                   uploadTask.addOnFailureListener(e -> {
                       Toast.makeText(getContext(), "Failed to upload to cloud.", Toast.LENGTH_SHORT).show();
                       Log.d(getTag(), e.toString());
                   }).addOnSuccessListener(taskSnapshot -> {
                       Toast.makeText(getContext(), "Photo uploaded to cloud!" ,Toast.LENGTH_SHORT).show();
                   });
               }
               @Override
               public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                   Toast.makeText(getContext(), "Failed to save photo" ,Toast.LENGTH_SHORT).show();
               }
           });
       });

        btnSelectFromFolder.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_OPEN_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_GET_IMAGE);
        });

        //bind to lifecycle:
        CameraX.bindToLifecycle(this, preview, imgCap);
    }

    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int)textureView.getRotation();

        switch(rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float)rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    CharSequence prediction = sendImagetoFirebase(imageBitmap);
                    txtResult.setText(prediction);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String sendImagetoFirebase(Bitmap image) {
        Log.d("7.1 Status: ", "send image to firebase");

        FirebaseCustomLocalModel localModel;
        FirebaseModelInterpreterOptions options;
        FirebaseModelInputOutputOptions inputOutputOptions;

//        localModel = new FirebaseCustomLocalModel.Builder().setAssetFilePath("restaurants-detector.tflite").build();
        localModel = new FirebaseCustomLocalModel.Builder().setAssetFilePath("restaurants-detector-v2.tflite").build();

        FirebaseModelInterpreter interpreter;
        try {
            options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, IMG_SIZE, IMG_SIZE, IMG_CHANNEL})
                    .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, IMG_CLASSES})
                    .build();

            float[][][][] input = aiModel.imagePreProcessing(image);

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
                            int bestId = aiModel.getIdOfBestRestaurant(probabilities);

                            //Retrieve restaurants from firestore
                            dbManagement.readData(new DatabaseManagement.FirestoreCallBack() {
                                @Override
                                public void onCallBack(ArrayList<Restaurant> restaurantArrayList) {
                                    Prediction prediction = aiModel.retrievePredictions(restaurantArrayList, bestId, probabilities[bestId]);
                                    if (prediction != null){
                                        finalOuput = String.format("Id: %s, Name: %s, Prob: %1.4f", prediction.getRestaurant().getId(), prediction.getRestaurant().getName(), prediction.getPrediction());
                                        Log.d("7.2 Status: ", finalOuput);
                                        txtResult.setText(finalOuput);

                                    }
                                }
                            });
                            Log.i("Best id: ", String.format("Id: %d,", bestId));
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
}