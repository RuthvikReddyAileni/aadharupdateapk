package com.example.aadharupdate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class mainscreen extends AppCompatActivity {

    Button camera,gallery ,submit;
    ImageView image ;
    Bitmap  imageBitmap ;
    static String data[] = new String[2] ;
    TextView text3 ;
    static String location = "" ;
    EditText aadharnum ;
    static String  aadhar="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        camera = findViewById(R.id.capture);
        gallery = findViewById(R.id.gallery);
        image = findViewById(R.id.imaage);
        submit = findViewById(R.id.submit);
        text3 = findViewById(R.id.detected);
        aadharnum = findViewById(R.id.aadharnum);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        try {
                detecttextfromimage();
            Handler handler = new Handler();
            aadhar =aadharnum.getText().toString();
            if (TextUtils.isEmpty(aadhar)) {
                aadharnum.setError("Aadhar number is Required");
                return;
            }
            if (aadhar.length()!=12) {
                aadharnum.setError("Aadhar is not in correct format");
                return;
            }
            Toast.makeText(mainscreen.this,"Please wait..",Toast.LENGTH_SHORT).show();
            handler. postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(getApplicationContext(),confirmationscreen.class));
                }
            }, 2000);
            }catch (Exception e){
             Toast.makeText(mainscreen.this,"Upload an image.",Toast.LENGTH_LONG).show();
        }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(mainscreen.this)
                        .galleryOnly()
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        } else {
            Uri uri = data.getData();
            image.setImageURI(uri);
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(mainscreen.this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder altdial = new AlertDialog.Builder(mainscreen.this);
        altdial.setMessage("Are you sure to quit the app?").setCancelable(false)
                .setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = altdial.create();
        alert.setTitle("Alert ! ");
        alert.show();
    }
    public void detecttextfromimage(){
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private  void displayTextFromImage(FirebaseVisionText firebaseVisionText){
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if(blockList.size()==0){
            Toast.makeText(mainscreen.this,"No text found",Toast.LENGTH_LONG).show();
        } else {
            String text1 = "";
            int i = 0;
            for (FirebaseVisionText.Block block: blockList){
                text1 = text1 + block.getText();
                text1 = text1 + "\n";
            }
            Pattern pattern = Pattern.compile("Address", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text1);
            boolean matchFound = matcher.find();
            int x = 0 , y = 0 ;
            if(matchFound){
                  x = text1.indexOf("Address");
                  y = text1.indexOf("5");
                  int q = 1 ;
                  while(q!=7){
                      char p = text1.charAt(y+1);
                      if (Character.isDigit(p)){
                          y++ ;
                          q++ ;
                    } else {
                          break;
                      }
                }
            }
            String line ="";
            for(int j = x+8 ; j<=y ; j++){
                line = line + text1.charAt(j);
            }
            location = line ;
        }
    }
}
