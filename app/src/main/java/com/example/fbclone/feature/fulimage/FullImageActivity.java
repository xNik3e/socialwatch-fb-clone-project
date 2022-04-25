package com.example.fbclone.feature.fulimage;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.fbclone.R;
import com.example.fbclone.data.remote.ApiClient;
import com.github.chrisbanes.photoview.PhotoView;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        String image = getIntent().getStringExtra("imageUrl");

        Uri imageUri = Uri.parse(image);
        PhotoView photoView = findViewById(R.id.photoview);

        if(imageUri.getAuthority() == null){
            if(image.contains("../")){
                image = ApiClient.BASE_URL+image;
                Glide.with(this).load(image).into(photoView);

            }else{
                Glide.with(this).load(Integer.parseInt(image)).into(photoView);

            }
        }else{
            Glide.with(this).load(image).into(photoView);
        }

    }
}