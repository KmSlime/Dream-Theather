package com.dream.dreamtheather.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dream.dreamtheather.Login;
import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Users;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.UserProfile;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

public class AccountTabFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_account_tab, container, false);
        initView(view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleSignIn();
        boolean isInit = googleApiClient.isConnected();
        if(googleApiClient != null) {
            if(isInit) {
                googleApiClient.stopAutoManage(getActivity());
                googleApiClient.disconnect();
                Log.v("AccountFrag"," Already connect");
            }
            else {
                Log.v("AccountFrag", " First connect");
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

    private void initView(View view) {

        View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = /*View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |*/ View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        imgAvata = view.findViewById(R.id.imgAvata);
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this::BackToMain);

        tvBalance = view.findViewById(R.id.tvBalance);
        tvLoyaltyPoint = view.findViewById(R.id.tvLoyaltyPoint);

        edtAddress = view.findViewById(R.id.edtAddress);
        edtPhoneNum = view.findViewById(R.id.edtPhoneNum);
        edtBirthDay = view.findViewById(R.id.edtBirthDay);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtUserFullName = view.findViewById(R.id.edtUserFullName);

        btnSignOut = view.findViewById(R.id.btnSignOut);
        btnSave = view.findViewById(R.id.btnSave);

        rad_group_gender = view.findViewById(R.id.rad_group_gender);
        radioFemale = view.findViewById(R.id.radioFemale);
        radioMale = view.findViewById(R.id.radioMale);

        //fire cloud
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //model
        users = new Users();

    }

    private void initGoogleSignIn(){

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(),this::onConnectionFailed)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();
    }
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("AccountFrag", "Connect Failed");
    }
    @Override
    public void onStart() {
        super.onStart();


        DisplayUser();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });

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
        String testURL = "https://www.bootdey.com/img/Content/avatar/avatar5.png";
        if(avt.equals("chưa có")) {
            avt = testURL;
        }
        Glide.with(getActivity())
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

    public void updateUser(){
        Toast.makeText(getActivity(), "Chưa có chức năng này!!", Toast.LENGTH_SHORT).show();
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
    }


    public void BackToMain(View view) {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    private void gotoLoginAcitivity() {
        startActivity(new Intent(getActivity(), Login.class));
        getActivity().finish();
    }

    public void ChangeAvatar(View view) {
        Toast.makeText(getActivity(), "Đây là chức năng thay đổi avt, hiện tại chức năng này chưa có", Toast.LENGTH_SHORT).show();
    }


}