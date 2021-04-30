package com.wineberryhalley.upthunderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.wineberryhalley.upthunder.updater.AuthGPlay;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_m);
        AuthGPlay.autenticateNow(this);

    }
}