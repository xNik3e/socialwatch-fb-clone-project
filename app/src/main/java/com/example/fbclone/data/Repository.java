package com.example.fbclone.data;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fbclone.data.remote.ApiError;
import com.example.fbclone.data.remote.ApiService;
import com.example.fbclone.feature.auth.LoginActivity;
import com.example.fbclone.feature.profile.ProfileActivity;
import com.example.fbclone.feature.search.SearchActivity;
import com.example.fbclone.model.GeneralResponse;
import com.example.fbclone.model.auth.AuthResponse;
import com.example.fbclone.model.friend.FriendResponse;
import com.example.fbclone.model.post.PostResponse;
import com.example.fbclone.model.profile.ProfileResponse;
import com.example.fbclone.model.search.SearchResponse;
import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private static Repository instance = null;
    private final ApiService apiService;

    private Repository(ApiService apiService){
        this.apiService = apiService;
    }

    public static Repository getRepository(ApiService apiService){
        if(instance == null){
            instance = new Repository(apiService);
        }
        return instance;
    }
    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo){
        MutableLiveData<AuthResponse> auth = new MutableLiveData<>();
        Call<AuthResponse> call = apiService.login(userInfo);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.isSuccessful()){
                   auth.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    AuthResponse authResponse = null;
                    try{
                       authResponse = gson.fromJson(response.errorBody().string(), AuthResponse.class);
                    }catch (IOException e){
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        authResponse = new AuthResponse(errorMessage.message, errorMessage.status);
                    }
                    auth.postValue(authResponse);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                AuthResponse authResponse = new AuthResponse(errorMessage.message, errorMessage.status);
                auth.postValue(authResponse);
            }
        });
        return auth;
    }

    public LiveData<ProfileResponse> fetchProfileInfo(Map<String, String> params){
        MutableLiveData<ProfileResponse> userInfo = new MutableLiveData<>();
        Call<ProfileResponse> call = apiService.fetchProfileInfo(params);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if(response.isSuccessful()){
                    userInfo.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    ProfileResponse profileResponse = null;
                    try{
                        profileResponse = gson.fromJson(response.errorBody().string(), ProfileResponse.class);
                    }catch (IOException e){
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        profileResponse = new ProfileResponse(errorMessage.message, errorMessage.status);
                    }
                    userInfo.postValue(profileResponse);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                ProfileResponse profileResponse = new ProfileResponse(errorMessage.message, errorMessage.status);
                userInfo.postValue(profileResponse);
            }
        });
        return userInfo;
    }


    public LiveData<GeneralResponse> uploadPost(MultipartBody multipartBody, Boolean isCoverOrProfileImage){
        MutableLiveData<GeneralResponse> postUpload = new MutableLiveData<>();
        Call<GeneralResponse> call = null;

        if(isCoverOrProfileImage){
            call = apiService.uploadImage(multipartBody);
        }else{
            call = apiService.uploadPost(multipartBody);
        }
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if(response.isSuccessful()){
                    postUpload.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    GeneralResponse generalResponse = null;
                    try{
                        generalResponse = gson.fromJson(response.errorBody().string(), GeneralResponse.class);
                    }catch (IOException e){
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        generalResponse = new GeneralResponse(errorMessage.message, errorMessage.status);
                    }
                    postUpload.postValue(generalResponse);
                }
            }
            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                GeneralResponse generalResponse = new GeneralResponse(errorMessage.message, errorMessage.status);
                postUpload.postValue(generalResponse);
            }
        });
        return postUpload;
    }

    public LiveData<SearchResponse> search(Map<String, String> params){
        MutableLiveData<SearchResponse> searchInfo = new MutableLiveData<>();
        Call<SearchResponse> call = apiService.search(params);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if(response.isSuccessful()){
                    searchInfo.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    SearchResponse searchResponse = null;
                    try{
                        searchResponse = gson.fromJson(response.errorBody().string(), SearchResponse.class);
                    }catch (IOException e){
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        searchResponse = new SearchResponse(errorMessage.message, errorMessage.status);
                    }
                    searchInfo.postValue(searchResponse);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                SearchResponse searchResponse = new SearchResponse(errorMessage.message, errorMessage.status);
                searchInfo.postValue(searchResponse);
            }
        });
        return searchInfo;
    }

    public LiveData<GeneralResponse> performOperation(ProfileActivity.PerformAction performAction){
        MutableLiveData<GeneralResponse> searchInfo = new MutableLiveData<>();
        Call<GeneralResponse> call = apiService.performAction(performAction);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if(response.isSuccessful()){
                    searchInfo.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    GeneralResponse generalResponse = null;
                    try{
                        generalResponse = gson.fromJson(response.errorBody().string(), GeneralResponse.class);
                    }catch (IOException e){
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        generalResponse = new GeneralResponse(errorMessage.message, errorMessage.status);
                    }
                    searchInfo.postValue(generalResponse);
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                GeneralResponse generalResponse = new GeneralResponse(errorMessage.message, errorMessage.status);
                searchInfo.postValue(generalResponse);
            }
        });
        return searchInfo;
    }

    public MutableLiveData<FriendResponse> loadFriends(String uid){
        MutableLiveData<FriendResponse> searchInfo = new MutableLiveData<>();
        Call<FriendResponse> call = apiService.loadFriends(uid);
        call.enqueue(new Callback<FriendResponse>() {
            @Override
            public void onResponse(Call<FriendResponse> call, Response<FriendResponse> response) {
                if(response.isSuccessful()){
                    searchInfo.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    FriendResponse friendResponse = null;
                    try{
                        friendResponse = gson.fromJson(response.errorBody().string(), FriendResponse.class);
                    }catch (IOException e){
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        friendResponse = new FriendResponse(errorMessage.message, errorMessage.status);
                    }
                    searchInfo.postValue(friendResponse);
                }
            }

            @Override
            public void onFailure(Call<FriendResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                FriendResponse friendResponse = new FriendResponse(errorMessage.message, errorMessage.status);
                searchInfo.postValue(friendResponse);
            }
        });
        return searchInfo;
    }

    public LiveData<PostResponse> getNewsFeed(Map<String, String> params){
        MutableLiveData<PostResponse> posts = new MutableLiveData<>();
        Call<PostResponse> call = apiService.getNewsFeed(params);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if(response.isSuccessful()){
                    posts.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    PostResponse postResponse = null;
                    try{
                        postResponse = gson.fromJson(response.errorBody().string(), PostResponse.class);
                    }catch (IOException e){
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        postResponse = new PostResponse(errorMessage.message, errorMessage.status);
                    }
                    posts.postValue(postResponse);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                PostResponse postResponse = new PostResponse(errorMessage.message, errorMessage.status);
                posts.postValue(postResponse);
            }
        });
        return posts;
    }

    public LiveData<PostResponse> getProfilePosts(Map<String, String> params){
        MutableLiveData<PostResponse> posts = new MutableLiveData<>();
        Call<PostResponse> call = apiService.loadProfilePosts(params);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if(response.isSuccessful()){
                    posts.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    PostResponse postResponse = null;
                    try{
                        postResponse = gson.fromJson(response.errorBody().string(), PostResponse.class);
                    }catch (IOException e){
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        postResponse = new PostResponse(errorMessage.message, errorMessage.status);
                    }
                    posts.postValue(postResponse);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                PostResponse postResponse = new PostResponse(errorMessage.message, errorMessage.status);
                posts.postValue(postResponse);
            }
        });
        return posts;
    }

}
