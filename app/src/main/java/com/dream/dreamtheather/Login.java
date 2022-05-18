package com.dream.dreamtheather;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
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
    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 1; /* unique request id */
    private static final int
            LOGIN = R.id.btnLogin,
            GOOGLE = R.id.btnLoginGoogle,
            FACEBOOK = R.id.btnLoginFacebook;

    public GoogleSignInClient mGoogleSignInClient;
    public GoogleSignInAccount account;

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
//        initialPrefs();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    @SuppressLint("ClickableViewAccessibility")
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
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    boolean checkAlreadyLoginWithGoogle() {
        account = GoogleSignIn.getLastSignedInAccount(this);
        return account != null;
    }

    public void btnRegister(View view) {
        startActivity(new Intent(this, Register.class));
        finish();
    }

    public void ResetPass(View view) {
//        startActivity(new Intent(this, ForgottenPassword.class));
        final EditText resetMail = new EditText(Login.this);
        final AlertDialog.Builder passWordResetDialog = new AlertDialog.Builder(Login.this);
        passWordResetDialog.setTitle("Quên mật khẩu?");
        passWordResetDialog.setMessage("Nhập email của bạn:");
        passWordResetDialog.setView(resetMail);

        passWordResetDialog
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
        passWordResetDialog
            .setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        passWordResetDialog.create().show();
    }

    private void signInGoogle() {
        initGoogleApi();
        boolean isSignIn = checkAlreadyLoginWithGoogle();
        if (isSignIn) {
            firebaseAuthWithGoogle(account);
            Log.d(TAG, "Google: Already sign in with google before");
        } else {
            Log.d(TAG, "Google: Not log in with google, start intent to pick an account");
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleLauncher.launch(signInIntent);
        }
    }

    ActivityResultLauncher<Intent> googleLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d(TAG, "Google: requestCode: ");
                    Log.d(TAG, "Google: resultCode: " + result.getResultCode());
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInGoogleResult(task);
                }
            });

    private void handleSignInGoogleResult(Task<GoogleSignInAccount> task) {
        task
        .addOnSuccessListener(this::firebaseAuthWithGoogle)
        .addOnFailureListener(e -> Log.e(TAG, "Google: Failed Task login Google", e))
        .addOnCompleteListener(
            new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    try {
                        if (task.isSuccessful()) {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                            Toast.makeText(getBaseContext(),
                                    "Đăng nhập với Google thành công!",Toast.LENGTH_LONG)
                                    .show();
                            gotoMain();

                        } else {
                            Toast.makeText(getBaseContext(),
                                    "Đăng nhập với Google thất bại!", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } catch (ApiException ex) {
                        // Google Sign In failed, update UI appropriately
                        Log.e(TAG, "Google: Google sign in failed", ex);
                        // ...
                    }
                }
            }
        );
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
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(task -> {
                    Toast.makeText(Login.this, "Đăng nhập bằng Google thành công!", Toast.LENGTH_LONG).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    MainActivity.user = user;
                    checkIfFirstTimeSignIn();
                    gotoMain();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Login.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "last log - error - Authentication Failed- " + account.getEmail());
                })
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.v(TAG, "signInWithCredential - Google:failure", task.getException());
                        Log.v(TAG, "signInWithCredential - Google:failure with Email: "+ account.getEmail());
                    } else {
                        Log.v(TAG, "signInWithCredential - Google:success - " + account.getEmail());
                    }
                })
                ;
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
                        Log.v(TAG, "Facebook: Success Login");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.v(TAG, "Facebook: Login Cancel");
                        Toast.makeText(Login.this, R.string.signin_cancel, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.v(TAG, "Facebook: Login Failed - cause:"+ exception.toString());
                        Toast.makeText(Login.this, R.string.signin_error + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.v(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.v(TAG, "signInWithCredential - Facebook: On Success Listener");
                        Toast.makeText(Login.this, "Đăng nhập bằng Facebook Thành công", Toast.LENGTH_SHORT).show();
//                        myPrefs.setIsSignIn(true);
                        checkIfFirstTimeSignIn();
                        gotoMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // If sign in fails, display a message to the user.
                        Log.v(TAG, "signInWithCredential - Facebook:On Failure Listener" + e.getMessage());
                        Toast.makeText(Login.this, "Đăng nhập Facebook thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.v(TAG, "signInWithCredential - Facebook:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.v(TAG, "signInWithCredential - Facebook:failure", task.getException());
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
        String userProvider = user.getProviderId();
        if(userProvider == FacebookAuthProvider.PROVIDER_ID){
            user.getProviderData().get(0).getDisplayName();
        }

        String fullname = "";
        if (user.getDisplayName() != null && !user.getDisplayName().matches("")) {
            fullname = user.getDisplayName();
        }
        info.setFullName(fullname);

        if (user.getPhotoUrl() != null) {
            info.setAvaUrl(user.getPhotoUrl().toString());
        } else {
            info.setAvaUrl("");
        }

        info.setUserType("Khách");
        info.setId(user.getUid());
        if (user.getEmail() == null) {
            info.setEmail("");
        } else {
            info.setEmail(user.getEmail());
        }

        info.setBirthDay("");
        info.setGender("");

        if(user.getPhoneNumber() == null)
            info.setPhoneNumber("");
        else
            info.setPhoneNumber(user.getPhoneNumber());

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
        info.setFullName(fullname);

        if (user.getPhotoUrl() != null) {
            info.setAvaUrl(user.getPhotoUrl().toString());
        } else {
            info.setAvaUrl("");
        }

        if (info.getId() == null || info.getId() == "") {
            info.setId(user.getUid());
        }
        if (user.getEmail() == null) {
            info.setEmail("");
        } else {
            info.setEmail(user.getEmail());
        }
        if (info.getBirthDay() == null) {
            info.setBirthDay("");
        }
        if (info.getGender() == null) {
            info.setGender("");
        }
        if (info.getPhoneNumber() == null) {
            info.setPhoneNumber("");
        }
        if (info.getAddress() == null) {
            info.setAddress("");
        }
        if(info.getBalance() <= 0)
            info.setBalance(0);

        ArrayList<Integer> idTicket = new ArrayList<>();
        if (info.getIdTicket() == null) {
            info.setIdTicket(idTicket);
        }
        info.setUserType("Khách");

        Log.v(TAG, "User Updated");

        sendUserInfo(info);
    }

    private void sendUserInfo(UserInfo info) {
        mDb.collection("user_info")
                .document(info.getId())
                .set(info)
                .addOnSuccessListener(aVoid -> {
                    Log.w(TAG, "addUserToDatabase:success");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "addUserToDatabase:failure", e);
                });
    }

}











