package com.knowyouringredients;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PreviewActivity extends AppCompatActivity {

    ImageView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        previewImage = findViewById(R.id.previewImage);
        Uri imageUri = getIntent().getData();
        if (imageUri != null) {
            previewImage.setImageURI(imageUri);
        }
    }
}
