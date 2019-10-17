package com.example.restaurantrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread splashThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1500);
                    Intent sideMenuActivity = new Intent(getApplicationContext(),SideMenuActivity.class);
                    startActivity(sideMenuActivity);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        splashThread.start();
    }
}
