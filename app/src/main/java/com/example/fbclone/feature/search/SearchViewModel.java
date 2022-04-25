package com.example.fbclone.feature.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.fbclone.data.Repository;
import com.example.fbclone.model.search.SearchResponse;

import java.util.Map;

public class SearchViewModel extends ViewModel {
    private Repository repository;

    public SearchViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<SearchResponse> search(Map<String, String> params){
        return this.repository.search(params);
    }
}
