package com.example.fbclone.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.fbclone.data.Repository;
import com.example.fbclone.data.remote.ApiClient;
import com.example.fbclone.data.remote.ApiService;
import com.example.fbclone.feature.auth.LoginViewModel;
import com.example.fbclone.feature.homepage.MainViewModel;
import com.example.fbclone.feature.postupload.PostUploadViewModel;
import com.example.fbclone.feature.profile.ProfileViewModel;
import com.example.fbclone.feature.search.SearchActivity;
import com.example.fbclone.feature.search.SearchViewModel;
import com.google.android.gms.common.api.Api;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final Repository repository;

    public ViewModelFactory() {
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        repository = Repository.getRepository(apiService);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(repository);
        } else if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(repository);
        } else if (modelClass.isAssignableFrom(PostUploadViewModel.class)) {
            return (T) new PostUploadViewModel(repository);
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(repository);
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(repository);
        } else
            throw new IllegalArgumentException("View Model not found");
    }
}
