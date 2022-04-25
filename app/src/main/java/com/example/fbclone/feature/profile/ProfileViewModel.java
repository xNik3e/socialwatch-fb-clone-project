package com.example.fbclone.feature.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.fbclone.data.Repository;
import com.example.fbclone.model.GeneralResponse;
import com.example.fbclone.model.post.PostResponse;
import com.example.fbclone.model.profile.ProfileResponse;

import java.util.Map;

import okhttp3.MultipartBody;

public class ProfileViewModel extends ViewModel {

    private Repository repository;

    public ProfileViewModel(Repository repository) {
        this.repository = repository;
    }
    public LiveData<ProfileResponse> fetchProfileInfo(Map<String, String> params){
        return repository.fetchProfileInfo(params);
    }

    public LiveData<PostResponse> getProfilePosts(Map<String, String> params){
        return repository.getProfilePosts(params);
    }

    public LiveData<GeneralResponse> uploadPost(MultipartBody body, Boolean isCoverOrProfileImage){
        return repository.uploadPost(body, isCoverOrProfileImage);
    }

    public LiveData<GeneralResponse> performAction(ProfileActivity.PerformAction performAction){
        return repository.performOperation(performAction);
    }
}
