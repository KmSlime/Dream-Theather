package com.Dream.AppDatVeXemPhim.screens.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.Dream.AppDatVeXemPhim.R;
import com.Dream.AppDatVeXemPhim.screens.activity.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_TIME = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(() -> {
            startActivity(MainActivity.class);
        }, SPLASH_DISPLAY_TIME);
    }
    private void startActivity(Class<?> classStart){
        startActivity(new Intent(this,classStart));
        finish();
    }

}