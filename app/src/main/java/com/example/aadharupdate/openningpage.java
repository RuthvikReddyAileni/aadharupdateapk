package com.example.aadharupdate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class openningpage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        handler. postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(getApplicationContext(),mainscreen.class));
            }
        }, 2000);
    }
}