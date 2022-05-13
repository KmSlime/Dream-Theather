package com.dream.dreamtheather;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dream.dreamtheather.Model.UserInfo;
import com.dream.dreamtheather.data.MyPrefs;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LogInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int
            LOGIN = R.id.btnLogin,
            GOOGLE = R.id.btnLoginGoogle,
            FACEBOOK = R.id.btnLoginFacebook;

    GoogleSignInClient mGoogleSignInClient;

    CallbackManager mCallbackManager;

    FirebaseAuth mAuth;
    FirebaseFirestore mDb;

    //store user info to local storage
    MyPrefs myPrefs;

    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.btnEyeShow)
    ImageButton btnEyeShow;
    @BindView(R.id.btnLoginFacebook)
    Button btnLoginFacebook;
    @BindView(R.id.btnLoginGoogle)
    Button btnLoginGoogle;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initEventButton();
        initialFireBase();
        initialPrefs();
        initGoogleApi();
//        initFacebookLogin();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "Is Sign In? : " + myPrefs.getIsSignIn());
        if (myPrefs.getIsSignIn())
            gotoMain();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case LOGIN:
                if (checkValidate()) {
                    Toast.makeText(Login.this, "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    SignIn();
                }
                break;
            case GOOGLE:
                signInGoogle();
                break;
            case FACEBOOK:
                signInFacebook();
                break;
        }

    }

    private boolean onTouch(View v, MotionEvent event) {
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


    private void initEventButton() {
        btnEyeShow.setOnTouchListener(this::onTouch);
        btnLogin.setOnClickListener(this);
        btnLoginGoogle.setOnClickListener(this);
        btnLoginFacebook.setOnClickListener(this);
    }

    private void initialFireBase() {
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
    }

    private void initialPrefs() {
        myPrefs = new MyPrefs(this);
        if (myPrefs.getIsRememberMe()) {
            String[] acc = myPrefs.getAccount();
            edtUsername.setText(acc[0]);
            edtPassword.setText(acc[1]);
        }
    }

    private void initGoogleApi() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    public void btnRegister(View view) {
        startActivity(new Intent(this, Register.class));
        finish();
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
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            if (task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

                Toast.makeText(this, "Đăng nhập với Google thành công!", Toast.LENGTH_LONG).show();
                gotoMain();

            } else {
                Toast.makeText(this, "Đăng nhập với Google thất bại!", Toast.LENGTH_LONG).show();
            }
        } catch (ApiException ex) {
            // Google Sign In failed, update UI appropriately
            Log.v(TAG, "Google sign in failed", ex);
            // ...
        }

    }

    private void gotoMain() {
        startActivity(new Intent(Login.this, MainActivity.class));
        finish();
    }

    public boolean checkValidate() {
        if (edtUsername.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    public void SignIn() {
        String email = Objects.requireNonNull(edtUsername).getText().toString().trim();
        String password = Objects.requireNonNull(edtPassword).getText().toString().trim();
        String[] acc = {email, password};
        myPrefs.setAccount(acc);

        if (validateAccount(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getInstance().getCurrentUser();
                            mA
                            if (user.isEmailVerified()) {
                                myPrefs.setIsSignIn(true);
                                Toast.makeText(Login.this, "Đăng nhập email thành công", Toast.LENGTH_SHORT).show();
                                gotoMain();
                            } else {
                                Toast.makeText(Login.this, "Vui lòng xác nhận email!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Login.this, "Lỗi đăng nhập: " + task.getException(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    private boolean validateAccount(String email, String password) {
        Boolean validate = true;

        if (email.isEmpty()) {
            edtUsername.setError(getString(R.string.email_empty));
            validate = false;
        } else if (!isValidEmail(email)) {
            edtUsername.setError(getString(R.string.email_invalid));
            validate = false;
        } else {
            edtUsername.setError(null);
        }

        if (password.isEmpty()) {
            edtUsername.setError(null);
            edtPassword.setError(getString(R.string.password_empty));
            validate = false;
        } else if (password.length() < 6) {
            edtUsername.setError(null);
            edtPassword.setError(getString(R.string.password_length));
            validate = false;
        } else {
            edtPassword.setError(null);
        }

        return validate;
    }

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        myPrefs.setIsSignIn(false);
                        // If sign in fails, display a message to the user.
                        Log.v(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(Login.this, "Xác thực không thành công!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Login.this, "Xác thực thành công!", Toast.LENGTH_LONG).show();
                        Log.v(TAG, "signInWithCredential:success - " + account.getEmail());
                        myPrefs.setIsSignIn(true);
                        checkIfFirstTimeSignIn();
                        gotoMain();
                    }
                })
                .addOnFailureListener(task -> {
                    Toast.makeText(Login.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                    Log.v(TAG, "last log - error - Authentication Failed- " + account.getEmail());
                });
    }

    private void signInFacebook() {
        mCallbackManager = CallbackManager.Factory.create();

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance()
                .registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.v(TAG, "Success Login");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.v(TAG, "Login Cancel");
                        Toast.makeText(Login.this, R.string.signin_cancel, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.v(TAG, "Success Failed");
                        Toast.makeText(Login.this, R.string.signin_error + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.v(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.v(TAG, "signInWithCredential:success");
                        Toast.makeText(Login.this, R.string.signin_success, Toast.LENGTH_SHORT).show();
                        myPrefs.setIsSignIn(true);
                        checkIfFirstTimeSignIn();
                        gotoMain();
                    } else {
                        myPrefs.setIsSignIn(false);
                        // If sign in fails, display a message to the user.
                        Log.v(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(Login.this, R.string.signin_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfFirstTimeSignIn() {
        FirebaseUser user = mAuth.getCurrentUser();

        DocumentReference docRef = mDb.collection("user_info").document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.i(TAG, "DocumentSnapshot data: " + document.getData());
                    UserInfo info = document.toObject(UserInfo.class);
                    updateOldUser(info, user);
                } else {
                    Log.i(TAG, "No such document");
                    UserInfo info = new UserInfo();
                    addNewUser(info, user);
                }
            } else {
                Log.v(TAG, "get failed with ", task.getException());
            }
        });
    }

    //for insert/update collection user_info
    private void addNewUser(UserInfo info, FirebaseUser user) {
        String fullname = "";
        if (user.getDisplayName() != null && !user.getDisplayName().matches("")) {
            fullname = user.getDisplayName();
        }

        if (user.getPhotoUrl() != null) {
            info.setAvaUrl(user.getPhotoUrl().toString());
        } else {
            info.setAvaUrl("");
        }

        info.setUserType("Khách");
        info.setId(user.getUid());
        info.setFullName(fullname);
        if (user.getEmail() == null) {
            info.setEmail("");
        } else {
            info.setEmail(user.getEmail());
        }
        info.setBirthDay("");
        info.setGender("");
        info.setPhoneNumber((user.getPhoneNumber() != null) ? "" : user.getPhoneNumber());
        info.setAddress("");
        info.setBalance(10000);
        ArrayList<Integer> idTicket = new ArrayList<>();
        info.setIdTicket(idTicket);

        Log.v(TAG, "New User Created");

        sendUserInfo(info);
    }

    private void updateOldUser(UserInfo info, FirebaseUser user) {
        String fullname = "";
        if (user.getDisplayName() != null && !user.getDisplayName().matches("")) {
            fullname = user.getDisplayName();
        }

        if (user.getPhotoUrl() != null) {
            info.setAvaUrl(user.getPhotoUrl().toString());
        } else {
            info.setAvaUrl("");
        }

        info.setFullName(fullname);
        if (user.getEmail() == null) {
            info.setEmail("");
        } else {
            info.setEmail(user.getEmail());
        }

        Log.v(TAG, "User Updated");

        sendUserInfo(info);
    }

    private void sendUserInfo(UserInfo info) {
        mDb.collection("user_info").document(info.getId())
                .set(info)
                .addOnSuccessListener(aVoid -> {
                    Log.w(TAG, "addUserToDatabase:success");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "addUserToDatabase:failure", e);
                });
    }

}











