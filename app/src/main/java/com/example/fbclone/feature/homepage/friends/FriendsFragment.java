package com.example.fbclone.feature.homepage.friends;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbclone.R;
import com.example.fbclone.feature.homepage.MainActivity;
import com.example.fbclone.feature.homepage.MainViewModel;
import com.example.fbclone.model.friend.Friend;
import com.example.fbclone.model.friend.FriendResponse;
import com.example.fbclone.model.friend.Request;
import com.example.fbclone.utils.ViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {


    private MainViewModel mainViewModel;
    private Context context;
    private RecyclerView friendRequestRecyV, friendsRecyV;
    private TextView friendsTitle, requestTitle, defaultTitle;

    private FriendAdapter friendAdapter;
    private FriendRequestAdapter friendRequestAdapter;
    private List<Request> requests = new ArrayList<>();
    private List<Friend> friends = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel = new ViewModelProvider((FragmentActivity) context, new ViewModelFactory()).get(MainViewModel.class);
        friendsRecyV = view.findViewById(R.id.friends_recycler_view);
        friendRequestRecyV = view.findViewById(R.id.friend_request_recycler_view);
        friendsTitle = view.findViewById(R.id.friend_title);
        requestTitle = view.findViewById(R.id.request_title);
        defaultTitle = view.findViewById(R.id.default_textView);

        friendAdapter = new FriendAdapter(context, friends);
        friendRequestAdapter = new FriendRequestAdapter(context, requests);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);

        friendsRecyV.setAdapter(friendAdapter);
        friendsRecyV.setLayoutManager(linearLayoutManager);

        friendRequestRecyV.setAdapter(friendRequestAdapter);
        friendRequestRecyV.setLayoutManager(linearLayoutManager1);

        loadFriends();
    }

    private void loadFriends() {
        ((MainActivity) getActivity()).showProgressBar();
        mainViewModel.loadFriends(FirebaseAuth.getInstance().getUid()).observe(getViewLifecycleOwner(), new Observer<FriendResponse>() {
            @Override
            public void onChanged(FriendResponse friendResponse) {
                ((MainActivity) getActivity()).hideProgressBar();
                loadData(friendResponse);
            }
        });
    }

    private void loadData(FriendResponse friendResponse) {
        if (friendResponse.getStatus() == 200) {
            friends.clear();
            friends.addAll(friendResponse.getResult().getFriends());
            friendAdapter.notifyDataSetChanged();

            requests.clear();
            requests.addAll(friendResponse.getResult().getRequests());
            friendRequestAdapter.notifyDataSetChanged();

            if (friendResponse.getResult().getFriends().size() > 0) {
                friendsTitle.setVisibility(View.VISIBLE);
            } else {
                friendsTitle.setVisibility(View.GONE);
            }

            if (friendResponse.getResult().getRequests().size() > 0) {
                requestTitle.setVisibility(View.VISIBLE);
            } else {
                requestTitle.setVisibility(View.GONE);
            }

            if (friendResponse.getResult().getFriends().size() == 0 && friendResponse.getResult().getRequests().size() == 0) {
                defaultTitle.setVisibility(View.VISIBLE);
                requestTitle.setVisibility(View.GONE);
                friendsTitle.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(context, friendResponse.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}