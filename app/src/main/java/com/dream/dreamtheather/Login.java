package com.dream.dreamtheather;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dream.dreamtheather.Model.UserHelperClass;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //ánh xạ
    ImageButton btnEyeShow;
    EditText edtPassword, edtUsername;
    Button btnLoginFacebook, btnLoginEmail, btnLogin;

    //google auth
    private GoogleApiClient googleApiClient;
    private static final int SIGN_IN = 1;

    // Write a message to the firebase database
    FirebaseDatabase database;
    DatabaseReference reference;

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

        //firebase
//        database = FirebaseDatabase.getInstance();

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
        startActivity(new Intent(this, ForgottenPassword.class));
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

    //firebase auth
    public void isValidate(){
        database = FirebaseDatabase.getInstance();
        reference =database.getReference("User");

        UserHelperClass userHelperClass = new UserHelperClass();
        //get all value
//        String userName = edtUsername.getText().toString();
//        String passWord = edtPassword.getText().toString();


//        if(edtUsername.getText().equals(database.getReference("User")))

    }


    public boolean checkValidate(){
        if (edtUsername.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty())
        {
            return true;
        }
        return false;
    }

    public boolean checkValidAccount(){
//        if (!true) //vài bửa sửa thành compare dữ liệu từ database
//        {
//            return true;
//        }
//        return false;
        return true;
    }

    public boolean IsRegister(){
        Intent getIntent = getIntent();
        if (edtUsername.getText().toString().compareTo(getIntent.getStringExtra("RegisterUser")) == 0
                && edtPassword.getText().toString().compareTo(getIntent.getStringExtra("RegisterPassword")) == 0)
        {
            return true;
        }
        return false;
    }

    public void btnLogin(View view) {
        if (checkValidate()) {
            Toast.makeText(getApplicationContext(), "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            if (checkValidAccount() || IsRegister()) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                onStop();
            } else
                Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        //get data login from register
        Intent getIntent = getIntent();
        String user = getIntent.getStringExtra("RegisterUser");
        String psd = getIntent.getStringExtra("RegisterPassword");
        edtUsername.setText(user);
        edtPassword.setText(psd);

    }
}











