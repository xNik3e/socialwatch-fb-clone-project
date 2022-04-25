package com.example.fbclone.feature.search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbclone.R;
import com.example.fbclone.data.remote.ApiClient;
import com.example.fbclone.feature.profile.ProfileActivity;
import com.example.fbclone.model.search.User;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    Context context;
    List<User> userList;

    public SearchAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        String userImage = "";
        Uri userImageUri = Uri.parse(user.getProfileUrl());
        if(userImageUri.getAuthority() == null){
            userImage = ApiClient.BASE_URL+user.getProfileUrl();
        }else{
            userImage = user.getProfileUrl();
        }

        if(!userImage.isEmpty()){
            Glide.with(context).load(userImage).placeholder(R.drawable.default_profile_placeholder).into(holder.image);
        }
        holder.profileName.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView profileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.user_image);
            profileName = itemView.findViewById(R.id.user_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", userList.get(getAdapterPosition()).getUid()));
        }
    }
}
