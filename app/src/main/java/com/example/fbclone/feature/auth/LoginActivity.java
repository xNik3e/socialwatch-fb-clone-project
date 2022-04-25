package com.example.fbclone.feature.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fbclone.R;
import com.example.fbclone.data.remote.ApiClient;
import com.example.fbclone.feature.homepage.MainActivity;
import com.example.fbclone.feature.postupload.PostUploadActivity;
import com.example.fbclone.feature.profile.ProfileViewModel;
import com.example.fbclone.model.auth.AuthResponse;
import com.example.fbclone.model.profile.ProfileResponse;
import com.example.fbclone.utils.ViewModelFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 846382;
    private static final String TAG = "LoginActivity";
    private SignInButton signInButton;
    private Button button;
    private ProgressDialog progressDialog;
    private ProfileViewModel privateViewModel;
    private LoginViewModel viewModel;
    private String  photoUrl = "", coverUrl = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("Signing you in ... Please wait");
        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(LoginViewModel.class);
        privateViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ProfileViewModel.class);
        button = findViewById(R.id.button_sign_in);
        signInButton = findViewById(R.id.button_google_sign_in);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        
        
        mAuth = FirebaseAuth.getInstance();
        
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressDialog.show();
        button.setVisibility(View.GONE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>(){

                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if(task.isSuccessful()){
                                        fetchUserInfo(task);
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }else{
            button.setVisibility(View.VISIBLE);
        }
    }

    private void fetchUserInfo(Task<String> task) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", FirebaseAuth.getInstance().getUid());
        params.put("current_state", "5");
        privateViewModel.fetchProfileInfo(params).observe(this, new Observer<ProfileResponse>() {

            @Override
            public void onChanged(ProfileResponse profileResponse) {
                if(profileResponse.getStatus() == 200){
                    if (!profileResponse.getProfile().getProfileUrl().isEmpty()) {
                        Uri profileUri = Uri.parse(profileResponse.getProfile().getProfileUrl());
                        if (profileUri.getAuthority() == null) {
                            photoUrl = ApiClient.BASE_URL + profileResponse.getProfile().getProfileUrl();
                        }else{
                            photoUrl = profileResponse.getProfile().getProfileUrl();
                        }
                    }
                    if(!profileResponse.getProfile().getCoverUrl().isEmpty()){
                        Uri coverUri = Uri.parse(profileResponse.getProfile().getCoverUrl());
                        if(coverUri.getAuthority() == null){
                            coverUrl = ApiClient.BASE_URL + profileResponse.getProfile().getCoverUrl();
                        }else{
                            coverUrl = profileResponse.getProfile().getCoverUrl();
                        }
                        passData(photoUrl, coverUrl, task);
                    }
                }else{
                    passData("", "", task);
                }
            }
        });
    }

    private void passData(String s, String s1, Task<String> task) {
        FirebaseUser user = mAuth.getCurrentUser();
        String token = task.getResult();
        if(s.isEmpty()){
            photoUrl = user.getPhotoUrl().toString();
        }
        viewModel.login(new UserInfo(
                user.getUid(),
                user.getDisplayName(),
                user.getEmail(),
                photoUrl,
                coverUrl,
                token
        ))
                .observe(LoginActivity.this, new Observer<AuthResponse>() {
                    @Override
                    public void onChanged(AuthResponse authResponse) {
                        progressDialog.hide();
                        Toast.makeText(LoginActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if(authResponse.getAuth() != null){
                            updateUI(user);
                        }else{
                            FirebaseAuth.getInstance().signOut();
                            mGoogleSignInClient.signOut();
                            updateUI(null);
                        }
                    }
                });
    }

    public static class UserInfo{
        String uid, name, email, profileUrl, coverUrl, userToken;

        public UserInfo(String uid, String name, String email, String profileUrl, String coverUrl, String userToken) {
            this.uid = uid;
            this.name = name;
            this.email = email;
            this.profileUrl = profileUrl;
            this.coverUrl = coverUrl;
            this.userToken = userToken;
        }
    }
}