package com.example.restaurantrecognition.ui.search;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;


import com.example.restaurantrecognition.ui.FragmentInteractionListener;
import id.zelory.compressor.Compressor;

import static androidx.core.content.ContextCompat.getExternalFilesDirs;

public class SearchFragment extends Fragment {

    private final int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

//    @BindView(R.id.btnSearchImage)
//    Button buttonSearchImage;

    @BindView(R.id.view_finder)
    TextureView textureView;

    @BindView(R.id.imgCapture)
    ImageButton imgBtn;

    private FragmentInteractionListener mListener;
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
                  // mListener.changeFragment(1);
               }
               @Override
               public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                   Toast.makeText(getContext(), "Failed to save photo" ,Toast.LENGTH_SHORT).show();
               }
           });

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

}