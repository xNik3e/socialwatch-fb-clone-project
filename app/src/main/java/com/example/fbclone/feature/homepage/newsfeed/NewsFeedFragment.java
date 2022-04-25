package com.example.fbclone.feature.homepage.newsfeed;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fbclone.R;
import com.example.fbclone.feature.homepage.MainActivity;
import com.example.fbclone.feature.homepage.MainViewModel;
import com.example.fbclone.model.post.PostResponse;
import com.example.fbclone.model.post.PostsItem;
import com.example.fbclone.utils.ViewModelFactory;
import com.example.fbclone.utils.adapter.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private MainViewModel viewModel;
    private SwipeRefreshLayout swipe;
    private PostAdapter postAdapter;
    private Context context;
    private List<PostsItem> postsItems;
    private int limit = 3;
    private int offset = 0;

    private Boolean isFirstLoading = true;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyv_newsfeed);
        swipe = view.findViewById(R.id.swipe);


        swipe.setOnRefreshListener(this);
        postsItems = new ArrayList<>();
        viewModel = new ViewModelProvider((FragmentActivity) context, new ViewModelFactory()).get(MainViewModel.class);

        LinearLayoutManager linearLayout = new LinearLayoutManager(context);

        recyclerView.setLayoutManager(linearLayout);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (isLastItemReached()) {
                    offset += limit;
                    fetchNews();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchNews();
    }

    private void fetchNews() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", FirebaseAuth.getInstance().getUid());
        params.put("limit", limit + "");
        params.put("offset", offset + "");

        ((MainActivity) getActivity()).showProgressBar();

        viewModel.getNewsFeed(params).observe(getViewLifecycleOwner(), new Observer<PostResponse>() {
            @Override
            public void onChanged(PostResponse postResponse) {
                ((MainActivity) getActivity()).hideProgressBar();
                if (postResponse.getStatus() == 200) {
                    if(swipe.isRefreshing()){
                        postsItems.clear();
                        postAdapter.notifyDataSetChanged();
                        swipe.setRefreshing(false);
                    }
                    postsItems.addAll(postResponse.getPosts());
                    if (isFirstLoading) {
                        postAdapter = new PostAdapter(context, postsItems);
                        recyclerView.setAdapter(postAdapter);
                    } else {
                        postAdapter.notifyItemRangeInserted(postsItems.size(), postResponse.getPosts().size());
                    }
                    if(postResponse.getPosts().size() == 0){
                        offset -= limit;
                    }
                    isFirstLoading = false;
                } else {
                    if(swipe.isRefreshing())
                        swipe.setRefreshing(false);
                    Toast.makeText(context, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        offset = 0;
        postsItems.clear();
        isFirstLoading = true;
    }


    private boolean isLastItemReached() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findLastCompletelyVisibleItemPosition();
        int numberofItems = postAdapter.getItemCount();

        return (position >= numberofItems - 1);
    }

    @Override
    public void onRefresh() {
        offset = 0;
        isFirstLoading = true;
        fetchNews();
    }
}