package com.example.aadharupdate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class success extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Toast.makeText(success.this,"Address updated successfully.",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(),mainscreen.class));
    }
}