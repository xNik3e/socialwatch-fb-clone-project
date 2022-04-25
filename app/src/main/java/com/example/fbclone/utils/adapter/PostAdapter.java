package com.example.fbclone.utils.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbclone.R;
import com.example.fbclone.data.remote.ApiClient;
import com.example.fbclone.model.post.PostsItem;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    List<PostsItem> postsItems;

    public PostAdapter(Context context, List<PostsItem> postsItems) {
        this.context = context;
        this.postsItems = postsItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostsItem postsItem = postsItems.get(position);
        holder.personName.setText(postsItem.getName());
        holder.date.setText(postsItem.getStatusTime());

        if(postsItem.getPrivacy() == 0){
            holder.privacyIcon.setImageResource(R.drawable.ic_friends);
        }else if(postsItem.getPrivacy() == 1){
            holder.privacyIcon.setImageResource(R.drawable.ic_only_me);
        }else{
            holder.privacyIcon.setImageResource(R.drawable.ic_public);
        }

        String profileIng = "";

        if(!postsItem.getProfileUrl().isEmpty()){
            if(Uri.parse(postsItem.getProfileUrl()).getAuthority() == null){
                profileIng = ApiClient.BASE_URL+postsItem.getProfileUrl();
            }else{
                profileIng = postsItem.getProfileUrl();
            }
        }
        Glide.with(context).load(profileIng).placeholder(R.drawable.default_profile_placeholder).into(holder.personImage);

        if(!postsItem.getStatusImage().isEmpty()){
            holder.statusImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(ApiClient.BASE_URL+postsItem.getStatusImage()).placeholder(R.drawable.default_profile_placeholder).into(holder.statusImage);
        }else{
            holder.statusImage.setVisibility(View.GONE);
        }

        if(postsItem.getPost().isEmpty()){
            holder.post.setVisibility(View.GONE);
        }else{
            holder.post.setVisibility(View.VISIBLE);
            holder.post.setText(postsItem.getPost());
        }


    }

    @Override
    public int getItemCount() {
        return postsItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView personImage, privacyIcon, statusImage;
        TextView personName, date, post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            personImage = itemView.findViewById(R.id.person_image);
            privacyIcon = itemView.findViewById(R.id.privacy_icon);
            statusImage = itemView.findViewById(R.id.status_img);
            date= itemView.findViewById(R.id.date);
            personName = itemView.findViewById(R.id.person_name);
            post = itemView.findViewById(R.id.post);

        }
    }
}
