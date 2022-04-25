package com.example.fbclone.feature.postupload;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.fbclone.data.Repository;
import com.example.fbclone.model.GeneralResponse;

import okhttp3.MultipartBody;

public class PostUploadViewModel extends ViewModel {
    private Repository repository;

    public PostUploadViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<GeneralResponse> uploadPost(MultipartBody body, Boolean isCoverOrProfileImage){
        return this.repository.uploadPost(body, isCoverOrProfileImage);
    }
}
