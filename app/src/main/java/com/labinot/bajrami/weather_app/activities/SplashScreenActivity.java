package com.labinot.bajrami.weather_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.labinot.bajrami.weather_app.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashScreenTheme);
        super.onCreate(savedInstanceState);

        Intent i = new Intent(SplashScreenActivity.this,MainActivity.class);
        startActivity(i);
        finish();

    }
}