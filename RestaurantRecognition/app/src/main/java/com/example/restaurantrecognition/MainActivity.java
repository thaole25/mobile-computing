package com.example.restaurantrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public Button button;

    static final int REQUEST_IMAGE_CAPTURE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btnCamera);

        button.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View view){
               Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               if (cameraIntent.resolveActivity(getPackageManager()) != null){
                   startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
               }
           }
        });
    }
}
