package com.example.professionalartbookjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URI;

public class MainActivity2 extends AppCompatActivity {
    ImageView imageView;
    EditText editText;
    Button saveButton;
    Button deleteButton;
    Button updateButton;
    Bitmap selectImage;
    String firstName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);



        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if (info.matches("new")){
            //Bitmap backroud = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_launcher_background);
            //imageView.setImageBitmap(backroud);
            deleteButton.setVisibility(View.INVISIBLE);
            updateButton.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        }else{
            deleteButton.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            String name = intent.getStringExtra("name");
            editText.setText(name);
            firstName = name;
            int position = intent.getIntExtra("position",0);
            imageView.setImageBitmap(MainActivity.artImageList.get(position));

        }

    }
    public void deleteRecord(View view){

        String deleteName = editText.getText().toString();
        getContentResolver().delete(ArtContentProvider.Content_URI,"name=?",new String[] {deleteName});

        Intent intent = new Intent(MainActivity2.this,MainActivity.class);
        startActivity(intent);

    }
    public void saveRecord(View view){
        String ArtName = editText.getText().toString();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte [] bytes = outputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ArtContentProvider.NAME,ArtName);
        contentValues.put(ArtContentProvider.IMAGE,bytes);

        getContentResolver().insert(ArtContentProvider.Content_URI,contentValues);

        Intent intent = new Intent(MainActivity2.this,MainActivity.class);
        startActivity(intent);

    }
    public void updateRecord(View view){
        //update
        String ArtName = editText.getText().toString();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte [] bytes = outputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ArtContentProvider.NAME,ArtName);
        contentValues.put(ArtContentProvider.IMAGE,bytes);


        getContentResolver().update(ArtContentProvider.Content_URI,contentValues,"name=?",new String[]{firstName});

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);


    }
    public void selecetImage(View view){
        if (ContextCompat.checkSelfPermission(MainActivity2.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity2.this,new String[] { Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 &&  resultCode ==  RESULT_OK && data != null){
            Uri image = data.getData();

            try {
                selectImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                imageView.setImageBitmap(selectImage);
            }catch (Exception e ){
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}