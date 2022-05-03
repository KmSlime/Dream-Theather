package com.dream.dreamtheather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //ánh xạ
    ImageButton btnEyeShow;
    EditText edtPassword, edtUsername;
    Button btnLoginFacebook, btnLoginEmail, btnLogin;
    ProgressBar progressBar;

    //google auth
    private GoogleApiClient googleApiClient;
    private static final int SIGN_IN = 1;

    // Write a message to the firebase database
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);

        //Img btn
        btnEyeShow = findViewById(R.id.btnEyeShow);
        btnLoginEmail = findViewById(R.id.btnLoginEmail);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
        btnLogin = findViewById(R.id.btnLogin);

        progressBar = findViewById(R.id.progressBar);

        //firebase
//        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        btnEyeShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                    case MotionEvent.ACTION_UP:
                        edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        return true;
                }
                return false;
            }
        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        btnLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN);
            }
        });

    }

    public void btnRegister(View view) {
        startActivity(new Intent(this, Register.class));
    }

    public void ResetPass(View view) {
//        startActivity(new Intent(this, ForgottenPassword.class));
        final EditText resetMail = new EditText(Login.this);
        final AlertDialog.Builder passWordResetDialog = new AlertDialog.Builder(Login.this);
        passWordResetDialog.setTitle("Thay đổi mật khẩu?");
        passWordResetDialog.setMessage("Nhập email của bạn:");
        passWordResetDialog.setView(resetMail);

        passWordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String mail = resetMail.getText().toString();
                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Login.this, "Đã gửi link reset về email của bạn", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        passWordResetDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        passWordResetDialog.create().show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                startActivity(new Intent(Login.this, UserProfile.class)); // sau này sẽ sửa dòng này
                finish();
            } else Toast.makeText(this, "Đăng nhập với Google thất bại!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkValidate(){
        if (edtUsername.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty())
        {
            return true;
        }
        return false;
    }
//
//    public boolean IsRegister(){
//        Intent getIntent = getIntent();
//        if (edtUsername.getText().toString().compareTo(getIntent.getStringExtra("RegisterUser")) == 0
//                && edtPassword.getText().toString().compareTo(getIntent.getStringExtra("RegisterPassword")) == 0)
//        {
//            return true;
//        }
//        return false;
//    }

    @Override
    protected void onStart() {
        super.onStart();

        //get data login from register
        Intent getIntent = getIntent();
        String user = getIntent.getStringExtra("edtRegisterEmail");
        String psd = getIntent.getStringExtra("RegisterPassword");
        edtUsername.setText(user);
        edtPassword.setText(psd);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidate()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    firebaseAuth.signInWithEmailAndPassword(edtUsername.getText().toString(), edtPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, UserProfile.class)); // sau này sẽ sửa dòng này
                                    finish();
                                }else{
                                    Toast.makeText(Login.this, "Lỗi đăng nhập: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                }
            }
        });

    }
}











