package com.dream.dreamtheather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.dream.dreamtheather.Model.Users;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;


public class UserProfile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView btnBack;
    private RoundedImageView imgAvata;
    private TextView tvBalance, tvLoyaltyPoint;
    private EditText edtAddress, edtPhoneNum, edtBirthDay, edtEmail, edtUserFullName;
    private Button btnSignOut, btnSave;
    private RadioGroup rad_group_gender;
    private RadioButton radioFemale, radioMale, radioButton;


    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    public static FirebaseUser firebaseUser;
    Users users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        imgAvata = findViewById(R.id.imgAvata);
        btnBack = findViewById(R.id.btnBack);

        tvBalance = findViewById(R.id.tvBalance);
        tvLoyaltyPoint = findViewById(R.id.tvLoyaltyPoint);

        edtAddress = findViewById(R.id.edtAddress);
        edtPhoneNum = findViewById(R.id.edtPhoneNum);
        edtBirthDay = findViewById(R.id.edtBirthDay);
        edtEmail = findViewById(R.id.edtEmail);
        edtUserFullName = findViewById(R.id.edtUserFullName);

        btnSignOut = findViewById(R.id.btnSignOut);
        btnSave = findViewById(R.id.btnSave);

        rad_group_gender = findViewById(R.id.rad_group_gender);
        radioFemale = findViewById(R.id.radioFemale);
        radioMale = findViewById(R.id.radioMale);

        //fire cloud
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //model
        users = new Users();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();



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

//            tvUserNameOfAcount.setText(account.getDisplayName());
//            tvEmailOfAcount.setText(account.getEmail());
//            tvHoTenOfAcount.setText(account.getDisplayName());
//
//            Picasso.get().load(account.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(imageAvt);
//        }else{
//            gotoLoginAcitivity();
        }
    }


    public void setValueUser(){
        String avt = users.getAvaUrl();

        Glide.with(UserProfile.this)
                .load(avt)
                .error(R.drawable.error)
                .placeholder(R.drawable.movie_boy)
                .into(imgAvata);

        tvBalance.setText(String.valueOf(users.getBalance()));
        tvLoyaltyPoint.setText(String.valueOf(users.getLoyaltyPoint()));

        edtUserFullName.setText(users.getFullName());
        edtAddress.setText(users.getAddress());
        edtPhoneNum.setText(users.getPhoneNumber());
        edtBirthDay.setText(users.getBirthDay());
        edtEmail.setText(users.getEmail());

        String gender = users.getGender().trim().toLowerCase();

        if(gender.equals(radioMale.getText().toString().toLowerCase())){
            radioMale.toggle();
        }else if(gender.equals(radioFemale.getText().toString().toLowerCase())){
            radioFemale.toggle();
        }
    }


    public void DisplayUser(){

        firebaseUser = firebaseAuth.getCurrentUser();
        String userID = firebaseUser.getUid();

        DocumentReference userGet = firebaseFirestore.collection("user_info").document(userID);
        userGet.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                users =documentSnapshot.toObject(Users.class);

                setValueUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "Error: " + e.getMessage());
            }
        });
//
//        firebaseFirestore.collection("user_info").whereEqualTo("userID", userID)
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for(QueryDocumentSnapshot document : task.getResult()){
//
//
//                    }
//                }else{
//                    Toast.makeText(UserProfile.this, "Lỗi", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//            }
//        });
    }


    @Override
    public void onStart() {
        super.onStart();

        DisplayUser();

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

    public void BackToMain(View view) {
    }
}