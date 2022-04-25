package com.example.fbclone.data.remote;

import com.example.fbclone.feature.auth.LoginActivity;
import com.example.fbclone.feature.profile.ProfileActivity;
import com.example.fbclone.model.GeneralResponse;
import com.example.fbclone.model.auth.AuthResponse;
import com.example.fbclone.model.friend.FriendResponse;
import com.example.fbclone.model.post.PostResponse;
import com.example.fbclone.model.profile.ProfileResponse;
import com.example.fbclone.model.search.SearchResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);

    @GET("loadprofileinfo")
    Call<ProfileResponse> fetchProfileInfo(@QueryMap Map<String, String> params);

    @POST("uploadpost")
    Call<GeneralResponse> uploadPost(@Body MultipartBody body);

    @POST("uploadImage")
    Call<GeneralResponse> uploadImage(@Body MultipartBody body);

    @GET("search")
    Call<SearchResponse> search(@QueryMap Map<String, String> params);

    @GET("loadfriends")
    Call<FriendResponse> loadFriends(@Query("uid") String uid);

    @GET("getnewsfeed")
    Call<PostResponse> getNewsFeed(@QueryMap Map<String, String> params);

    @GET("loadProfilePosts")
    Call<PostResponse> loadProfilePosts(@QueryMap Map<String, String> params);

    @POST("performaction")
    Call<GeneralResponse> performAction(@Body ProfileActivity.PerformAction performAction);

}
