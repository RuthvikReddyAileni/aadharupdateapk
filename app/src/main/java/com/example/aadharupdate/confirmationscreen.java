package com.example.aadharupdate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class confirmationscreen extends AppCompatActivity {

    Button clickhere , updateaddress;
    TextView city , state ;
    FusedLocationProviderClient fusedLocationProviderClient;
    static  Double currentLattitude , currentlongitude , recivedlattitude , recivedlongitude ;
    EditText housenum , streetname , landmark ;
    static String housenum1 , streetname1 , landmark1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmationscreen);
        clickhere = findViewById(R.id.clickscreen);
        updateaddress = findViewById(R.id.update);
        state = findViewById(R.id.state);
        housenum = findViewById(R.id.housenum);
        streetname = findViewById(R.id.streetname);
        landmark = findViewById(R.id.landmark);


        state.setText(mainscreen.location);
        updateaddress.setEnabled(false);






        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        clickhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(confirmationscreen.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(confirmationscreen.this
                        , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(confirmationscreen.this, "Please wait...", Toast.LENGTH_SHORT).show();
                    getLocation();
                    updateaddress.setEnabled(true);
                    updateaddress.setBackgroundColor(Color.parseColor("#9C9898"));
                    updateaddress.setTextColor(Color.parseColor("#000000"));

                } else {
                    ActivityCompat.requestPermissions(confirmationscreen.this
                            , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    ActivityCompat.requestPermissions(confirmationscreen.this
                            , new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
                }


            }

        });
        updateaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                housenum1 = housenum.getText().toString();
                streetname1 = streetname.getText().toString();
                landmark1 = landmark.getText().toString();
               if (TextUtils.isEmpty(housenum1)) {
                    housenum.setError("Aadhar number is Required");
                    return;
                }
                if (TextUtils.isEmpty(streetname1)) {
                    streetname.setError("Aadhar number is Required");
                    return;
                }
                if (TextUtils.isEmpty(landmark1)) {
                    landmark.setError("Aadhar number is Required");
                    return;
                }
                GeoLocation geolocation = new GeoLocation();
                geolocation.getAddress(mainscreen.location,getApplicationContext(),new GeoHandler());


                //Toast.makeText(confirmationscreen.this,"Current Latitude and longitude :"+currentLattitude +","+ currentlongitude,Toast.LENGTH_LONG).show();


                Handler handler = new Handler();
                handler. postDelayed(new Runnable() {
                    public void run() {
                  //      Toast.makeText(confirmationscreen.this,"Received Latitude and longitude :"+recivedlattitude +","+ recivedlongitude,Toast.LENGTH_LONG).show();
                    }
                }, 1000);

                if(recivedlattitude==currentLattitude && recivedlongitude ==currentlongitude){
                    update();
                }else {
                    Toast.makeText(confirmationscreen.this,"Error occurred , try again later",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),mainscreen.class));
                }




            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(confirmationscreen.this,
                            Locale.getDefault());

                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                        currentLattitude = addresses.get(0).getLatitude();
                        currentlongitude = addresses.get(0).getLongitude();
                        Toast.makeText(confirmationscreen.this, "Successfully got your current location.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(confirmationscreen.this, "Error occurred due to no internet connection.Try again.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private class GeoHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String address ,data1="",data2="";
            switch (msg.what){
                case 1 :
                    Bundle bundle = msg.getData();
                    address = bundle.getString("address");
                     data1 = bundle.getString("address1");
                     data2 = bundle.getString("address2");
                    recivedlattitude = Double.parseDouble(data1);
                    recivedlongitude = Double.parseDouble(data2);
                    break;
                default:
                    address=null;
            }
            if(data1!=""&&data2!=""){
           // Toast.makeText(confirmationscreen.this,"The L and L of received address:"+data1+","+data2,Toast.LENGTH_LONG).show();
             }
        }
    }
    public void update(){
        Map<String, Object> data1 = new HashMap<>();
        data1.put("New Address:",housenum1+","+streetname1+","+landmark1+","+mainscreen.location);


        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("Aadhar Address Update").document(mainscreen.aadhar)
                .set(data1, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(confirmationscreen.this,"Data entered into database.",Toast.LENGTH_LONG).show();
                        Log.d("joinpage_3", "DocumentSnapshot successfully written!");
                        startActivity(new Intent(getApplicationContext(),success.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(confirmationscreen.this,"Error in database",Toast.LENGTH_LONG).show();
                        Log.w("joinpage_3", "Error writing document", e);
                    }
                });

    }
}
