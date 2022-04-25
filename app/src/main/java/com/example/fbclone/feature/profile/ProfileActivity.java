package com.example.fbclone.feature.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.fbclone.R;
import com.example.fbclone.data.remote.ApiClient;
import com.example.fbclone.feature.fulimage.FullImageActivity;
import com.example.fbclone.feature.postupload.PostUploadActivity;
import com.example.fbclone.feature.search.SearchActivity;
import com.example.fbclone.model.GeneralResponse;
import com.example.fbclone.model.post.PostResponse;
import com.example.fbclone.model.post.PostsItem;
import com.example.fbclone.model.profile.Profile;
import com.example.fbclone.model.profile.ProfileResponse;
import com.example.fbclone.utils.ViewModelFactory;
import com.example.fbclone.utils.adapter.PostAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnDismissListener, SwipeRefreshLayout.OnRefreshListener {

    private int current_state = 0;
    /*
     *
     * 0 = profile is still loading
     * 1 = two people are friends (unfriend)
     * 2 = we have sent friend request to that person (cancel request)
     * 3 = we have received friend request from that person (reject or accept request)
     * 4 = we are unknown (you can send request)
     * 5 = our own profile
     *
     * */


    private String uid = "", profileUrl = "", coverUrl = "";

    private Button profileOptionButton;
    private ImageView profileImage, coverImage;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipe;
    private List<PostsItem> postsItems;
    private Boolean isCoverImage = false;
    private ProgressDialog progressDialog;
    private int limit = 3;
    private int offset = 0;

    private Boolean isFirstLoading = true;

    private ProfileViewModel viewModel;

    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ProfileViewModel.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.setCancelable(false);


        profileOptionButton = findViewById(R.id.profile_action_button);
        profileImage = findViewById(R.id.profileImage);
        coverImage = findViewById(R.id.profileCover);
        toolbar = findViewById(R.id.my_toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        recyclerView = findViewById(R.id.recycle_view_profile);
        progressBar = findViewById(R.id.progress_bar_profile);
        progressDialog.setMessage("Please wait ...");
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (isLastItemReached()) {
                    offset += limit;
                    getProfilePost();
                }
            }
        });
        postsItems = new ArrayList<>();

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();
            }
        });

        uid = getIntent().getStringExtra("uid");

        if (uid.equals(FirebaseAuth.getInstance().getUid())) {
            current_state = 5;
            profileOptionButton.setText("Edit profile");
        } else {
            // find current state from backend
            profileOptionButton.setText("Loading...");
            profileOptionButton.setEnabled(false);
        }
        fetchProfileInfo();

    }

    private void fetchProfileInfo() {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("userId", FirebaseAuth.getInstance().getUid());
        if (current_state == 5) {
            params.put("current_state", current_state + "");
        } else {
            params.put("profileId", uid);
        }
        viewModel.fetchProfileInfo(params).observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(ProfileResponse profileResponse) {
                progressDialog.hide();
                if (profileResponse.getStatus() == 200) {
                    collapsingToolbarLayout.setTitle(profileResponse.getProfile().getName());
                    profileUrl = profileResponse.getProfile().getProfileUrl();
                    coverUrl = profileResponse.getProfile().getCoverUrl();

                    current_state = Integer.parseInt(profileResponse.getProfile().getState());

                    if (!profileUrl.isEmpty()) {
                        Uri profileUri = Uri.parse(profileUrl);
                        if (profileUri.getAuthority() == null) {
                            profileUrl = ApiClient.BASE_URL + profileUrl;
                        }
                        Glide.with(ProfileActivity.this).load(profileUrl).into(profileImage);
                    }else{
                        profileUrl = R.drawable.default_profile_placeholder+"";
                    }
                    if (!coverUrl.isEmpty()) {
                        Uri coverUri = Uri.parse(coverUrl);
                        if (coverUri.getAuthority() == null) {
                            coverUrl = ApiClient.BASE_URL + coverUrl;
                        }
                        Glide.with(ProfileActivity.this).load(coverUrl).into(coverImage);
                    }else{
                        coverUrl = R.drawable.cover_picture_placeholder+"";
                    }

                    if (current_state == 0) {
                        profileOptionButton.setText("Loading");
                        profileOptionButton.setEnabled(false);
                        return;
                    } else if (current_state == 1) {
                        profileOptionButton.setText("You are friends");
                    } else if (current_state == 2) {
                        profileOptionButton.setText("Cancel request");
                    } else if (current_state == 3) {
                        profileOptionButton.setText("Accept Request");
                    } else if (current_state == 4) {
                        profileOptionButton.setText("Send Request");
                    } else if (current_state == 5) {
                        profileOptionButton.setText("Edit profile");
                    }

                    profileOptionButton.setEnabled(true);
                    loadProfileOptionButton();

                    getProfilePost();
                } else {
                    Toast.makeText(ProfileActivity.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean isLastItemReached() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findLastCompletelyVisibleItemPosition();
        int numberofItems = postAdapter.getItemCount();

        return (position >= numberofItems - 1);
    }

    private void getProfilePost() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("limit", limit+"");
        params.put("offset", offset+"");
        params.put("current_state", current_state + "");
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getProfilePosts(params).observe(this, new Observer<PostResponse>() {
            @Override
            public void onChanged(PostResponse postResponse) {
                progressBar.setVisibility(View.GONE);
                if (postResponse.getStatus() == 200) {
                    if(swipe.isRefreshing()){
                        postsItems.clear();
                        postAdapter.notifyDataSetChanged();
                        swipe.setRefreshing(false);
                    }
                    postsItems.addAll(postResponse.getPosts());
                    if (isFirstLoading) {
                        postAdapter = new PostAdapter(ProfileActivity.this, postsItems);
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
                    Toast.makeText(ProfileActivity.this, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadProfileOptionButton() {
        profileOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileOptionButton.setEnabled(false);
                if (current_state == 5) {
                    CharSequence[] options = new CharSequence[]{"Change Cover Image", "Change Profile Image", "View Cover Image", "View Profile Image"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                isCoverImage = true;
                                selectImage();
                            } else if (which == 1) {
                                isCoverImage = false;
                                selectImage();
                            } else if (which == 2) {
                                viewFullImage(coverImage, coverUrl);
                            } else if (which == 3) {
                                viewFullImage(profileImage, profileUrl);
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 4) {
                    CharSequence[] options = new CharSequence[]{"Send Friend Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 3) {
                    CharSequence[] options = new CharSequence[]{"Accept Friend Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();

                } else if (current_state == 2) {
                    CharSequence[] options = new CharSequence[]{"Cancel Friend Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 1) {
                    CharSequence[] options = new CharSequence[]{"Unfriend"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                }
            }
        });
    }

    private void performAction() {
        progressDialog.show();
        viewModel.performAction(new PerformAction(current_state + "", FirebaseAuth.getInstance().getUid(),
                uid)).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                progressDialog.hide();
                if (generalResponse.getStatus() == 200) {
                    profileOptionButton.setEnabled(true);
                    if (current_state == 4) {
                        current_state = 2;
                        profileOptionButton.setText("Cancel Request");
                    } else if (current_state == 3) {
                        current_state = 1;
                        profileOptionButton.setText("You are Friends");
                    } else if (current_state == 2) {
                        current_state = 4;
                        profileOptionButton.setText("Send Request");
                    } else if (current_state == 1) {
                        current_state = 4;
                        profileOptionButton.setText("Send Request");
                    }
                } else {
                    profileOptionButton.setEnabled(false);
                    profileOptionButton.setText("Error");
                }
            }
        });

    }

    private void viewFullImage(ImageView coverImage, String coverUrl) {
        Intent intent = new Intent(ProfileActivity.this, FullImageActivity.class);
        intent.putExtra("imageUrl", coverUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(coverImage, coverUrl);
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pairs);
            startActivity(intent, activityOptions.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void selectImage() {
        ImagePicker.create(this).single().folderMode(true).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image selectedImage = ImagePicker.getFirstImageOrNull(data);

            try {
                File compressedImageFile = new Compressor(this).setQuality(75).compressToFile(new File(selectedImage.getPath()));
                uploadImage(compressedImageFile);
            } catch (IOException e) {
                Toast.makeText(this, "Image Picker Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(File compressedImageFile) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("uid", FirebaseAuth.getInstance().getUid());
        builder.addFormDataPart("isCoverImage", isCoverImage + "");
        builder.addFormDataPart("file", compressedImageFile.getName(), RequestBody.create(compressedImageFile,
                MediaType.parse("multipart/form-data")));

        progressDialog.show();

        viewModel.uploadPost(builder.build(), true).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                progressDialog.hide();
                Toast.makeText(ProfileActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (generalResponse.getStatus() == 200) {
                    if (isCoverImage) {
                        Glide.with(ProfileActivity.this)
                                .load(ApiClient.BASE_URL + generalResponse.getExtra()).into(coverImage);
                    } else {
                        Glide.with(ProfileActivity.this)
                                .load(ApiClient.BASE_URL + generalResponse.getExtra()).into(profileImage);
                    }
                }
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        profileOptionButton.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        offset = 0;
        postsItems.clear();
        isFirstLoading = true;
    }

    @Override
    public void onRefresh() {
        offset = 0;
        isFirstLoading = true;
        getProfilePost();
    }

    public static class PerformAction {
        String operationType, uid, profileId;

        public PerformAction(String operationType, String uid, String profileId) {
            this.operationType = operationType;
            this.uid = uid;
            this.profileId = profileId;
        }

    }
}