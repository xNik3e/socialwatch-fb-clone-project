package com.example.fbclone.feature.postupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.fbclone.R;
import com.example.fbclone.data.remote.ApiClient;
import com.example.fbclone.feature.homepage.MainActivity;
import com.example.fbclone.feature.profile.ProfileActivity;
import com.example.fbclone.feature.profile.ProfileViewModel;
import com.example.fbclone.model.GeneralResponse;
import com.example.fbclone.model.profile.ProfileResponse;
import com.example.fbclone.utils.ViewModelFactory;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostUploadActivity extends AppCompatActivity {

    private AppCompatSpinner spinner;
    private PostUploadViewModel postUploadViewModel;
    private TextView postTxt;
    private TextInputEditText textInputEditText;
    private int privacy_label = 0;
    ImageView addImage, previewImage, profileImage;

    private ProgressDialog progressDialog;
    private Boolean isImageSelected = false;

    private File compressedImageFile = null;

    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);

        profileImage = findViewById(R.id.profileImage);

        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ProfileViewModel.class);
        postUploadViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(PostUploadViewModel.class);
        spinner = findViewById(R.id.spinner_privacy);
        postTxt = findViewById(R.id.postbtn);
        textInputEditText = findViewById(R.id.input_post);
        addImage = findViewById(R.id.add_image);
        previewImage = findViewById(R.id.image_prev);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        previewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        fetchProfilePicture();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("Uploading post ... Please wait!");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedTextView = (TextView) view;
                if (selectedTextView != null) {
                    selectedTextView.setTextColor(Color.CYAN);
                    selectedTextView.setTypeface(null, Typeface.BOLD);
                }
                privacy_label = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                privacy_label = 0;
            }
        });

        postTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = textInputEditText.getText().toString();
                String userId = FirebaseAuth.getInstance().getUid();

                if (status.trim().length() > 0 || isImageSelected) {
                    progressDialog.show();
                    MultipartBody.Builder builder = new MultipartBody.Builder();

                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("post", status);
                    builder.addFormDataPart("postUserId", userId);
                    builder.addFormDataPart("privacy", privacy_label + "");

                    if(isImageSelected){
                        builder.addFormDataPart("file",
                                compressedImageFile.getName(),
                                RequestBody.create(compressedImageFile, MediaType.parse("multipart/form-data")));
                    }

                    MultipartBody multipartBody = builder.build();

                    postUploadViewModel.uploadPost(multipartBody, false).observe(PostUploadActivity.this, new Observer<GeneralResponse>() {
                        @Override
                        public void onChanged(GeneralResponse generalResponse) {
                            progressDialog.hide();
                            Toast.makeText(PostUploadActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            if (generalResponse.getStatus() == 200) {
                                onBackPressed();
                            }
                        }
                    });
                } else {
                    Toast.makeText(PostUploadActivity.this, "Please provide your status", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchProfilePicture() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", FirebaseAuth.getInstance().getUid());
        params.put("current_state", "5");
        viewModel.fetchProfileInfo(params).observe(this, new Observer<ProfileResponse>() {

            @Override
            public void onChanged(ProfileResponse profileResponse) {
                if(profileResponse.getStatus() == 200){
                    if (!profileResponse.getProfile().getProfileUrl().isEmpty()) {
                        Uri profileUri = Uri.parse(profileResponse.getProfile().getProfileUrl());
                        String profileUrl = "";
                        if (profileUri.getAuthority() == null) {
                            profileUrl = ApiClient.BASE_URL + profileResponse.getProfile().getProfileUrl();
                        }else{
                            profileUrl = profileResponse.getProfile().getProfileUrl();
                        }
                        Glide.with(PostUploadActivity.this).load(profileUrl).into(profileImage);
                    }
                }else{
                    //nothing
                }
            }
        });
    }

    private void selectImage() {
        ImagePicker.create(this).single().folderMode(true).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ImagePicker.shouldHandle(requestCode, resultCode, data)){
            Image selectedImage = ImagePicker.getFirstImageOrNull(data);

            try{
                compressedImageFile = new Compressor(this).setQuality(75).compressToFile(new File(selectedImage.getPath()));
                isImageSelected = true;
                addImage.setVisibility(View.GONE);
                previewImage.setVisibility(View.VISIBLE);
                Glide.with(PostUploadActivity.this)
                        .load(selectedImage.getPath())
                        .into(previewImage);
            }catch(IOException e){
                previewImage.setVisibility(View.GONE);
                addImage.setVisibility(View.VISIBLE);
            }
        }
    }
}