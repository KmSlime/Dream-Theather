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
import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView imageAvt;
    private TextView txtUserNameOfAcount;
    private TextView txtEmailOfAcount;
    private TextView txtHoTenOfAcount;
    private TextView txtBirthdayOfAcount;
    private TextView txtGenderOfAcount;
    private Button btnSignOut;

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        imageAvt = findViewById(R.id.imageAvt);
        txtUserNameOfAcount = findViewById(R.id.txtUserNameOfAcount);
        txtEmailOfAcount = findViewById(R.id.txtEmailOfAcount);
        txtHoTenOfAcount = findViewById(R.id.txtHoTenOfAcount);
        txtBirthdayOfAcount = findViewById(R.id.txtBirthdayOfAcount);
        txtGenderOfAcount = findViewById(R.id.txtGenderOfAcount);
        btnSignOut = findViewById(R.id.btnSignOut);


        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()) gotoLoginAcitivity();
                        else Toast.makeText(UserProfile.this, "Đăng xuất thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void gotoLoginAcitivity() {
        startActivity(new Intent(this, Login.class));
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();

            txtUserNameOfAcount.setText(account.getDisplayName());
            txtEmailOfAcount.setText(account.getEmail());
            txtHoTenOfAcount.setText(account.getDisplayName());

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