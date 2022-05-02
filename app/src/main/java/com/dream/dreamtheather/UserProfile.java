package com.dream.dreamtheather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


public class UserProfile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView imageAvt;
    private TextView tvUserNameOfAcount;
    private TextView tvEmailOfAcount;
    private TextView tvHoTenOfAcount;
    private TextView tvBirthdayOfAcount;
    private TextView tvGenderOfAcount;
    private Button btnSignOut;

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        imageAvt = findViewById(R.id.imageAvt);
        tvUserNameOfAcount = findViewById(R.id.tvUserNameOfAcount);
        tvEmailOfAcount = findViewById(R.id.tvEmailOfAcount);
        tvHoTenOfAcount = findViewById(R.id.tvHoTenOfAcount);
        tvBirthdayOfAcount = findViewById(R.id.tvBirthdayOfAcount);
        tvGenderOfAcount = findViewById(R.id.tvGenderOfAcount);
        btnSignOut = findViewById(R.id.btnSignOut);


        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                gotoLoginAcitivity();
//                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//                        if(status.isSuccess()) gotoLoginAcitivity();
//                        else Toast.makeText(UserProfile.this, "Đăng xuất thất bại!", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });

    }

    private void gotoLoginAcitivity() {
        startActivity(new Intent(UserProfile.this, Login.class));
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();

            tvUserNameOfAcount.setText(account.getDisplayName());
            tvEmailOfAcount.setText(account.getEmail());
            tvHoTenOfAcount.setText(account.getDisplayName());

            Picasso.get().load(account.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(imageAvt);
        }else{
            gotoLoginAcitivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(optionalPendingResult.isDone())
        {
            GoogleSignInResult result = optionalPendingResult.get();
            handleSignInResult(result);
        }else
        {
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }


    }
}