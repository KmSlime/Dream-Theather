package com.dream.dreamtheather.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import com.dream.dreamtheather.Model.UserInfo;
import com.dream.dreamtheather.Model.Users;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.UserProfile;
import com.dream.dreamtheather.data.MyPrefs;
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
import com.google.protobuf.StringValue;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountTabFragment extends Fragment {

    private static final String TAG ="AccountTab";
    MyPrefs myPrefs;
    FirebaseFirestore mDb;
    FirebaseAuth mAuth;
    FirebaseUser user;

    public static AccountTabFragment newInstance() {
        AccountTabFragment fragment = new AccountTabFragment();
        return fragment;
    }

    @BindView(R.id.btnBack)             ImageView btnBack;
    @BindView(R.id.imgAvata)            RoundedImageView imgAvatar;
    @BindView(R.id.tvBalance)           TextView tvBalance;
    @BindView(R.id.tvLoyaltyPoint)      TextView tvLoyaltyPoint;
    @BindView(R.id.edtAddress)          EditText edtAddress;
    @BindView(R.id.edtPhoneNum)         EditText edtPhoneNum;
    @BindView(R.id.edtBirthDay)         EditText edtBirthDay;
    @BindView(R.id.edtEmail)            EditText edtEmail;
    @BindView(R.id.edtUserFullName)     EditText edtUserFullName;
    @BindView(R.id.btnSignOut)          Button btnSignOut;
    @BindView(R.id.btnSave)             Button btnSave;
    @BindView(R.id.rad_group_gender)    RadioGroup rad_group_gender;
    @BindView(R.id.radioFemale)         RadioButton radioFemale;
    @BindView(R.id.radioMale)           RadioButton radioMale;

    @OnClick(R.id.btnSignOut)
    void signOut() {
        mAuth.signOut();
        myPrefs.setIsSignIn(false);
        myPrefs.setIsAdmin(false);
        Log.v(TAG,"sign out");
        getUserInfo();
//        updateSignInOutUI();
        gotoLoginActivity();
//        ((MainActivity)getActivity()).restartHomeScreen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        myPrefs = new MyPrefs(getContext());

        mAuth = FirebaseAuth.getInstance();

        mDb = ((MainActivity)getActivity()).mDb;

        Log.v(TAG,"Load view Account");

        getUserInfo();
        updateSignInOutUI();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void getUserInfo(){
        user = ((MainActivity)getActivity()).user;
        if (user != null) {
            Log.v(TAG,"Info User: " + user.getDisplayName());
            Log.v(TAG,"Info User: " + user.getEmail());
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            if(photoUrl == null){
                Glide.with(this)
                        .load(R.drawable.movie_boy)
                        .into(imgAvatar);
            }
            else{
                Glide.with(this)
                        .load(Uri.parse(photoUrl.toString()))
                        .into(imgAvatar);
            }


            if(name == null || name.matches("")){
                edtEmail.setText(email);
            }
            else{
                edtUserFullName.setText(name);
            }
        }
        else{
            edtUserFullName.setText("@anonymous");
            Glide.with(this)
                    .load(R.drawable.movie_pop_corn)
                    .into(imgAvatar);
        }
    }

    private void updateSignInOutUI(){
        if(myPrefs.getIsSignIn()){
//            mAddAccountButton.hide();
//            mSuggestion.setVisibility(View.GONE);
//            btnSignout.setVisibility(View.VISIBLE);
//            mProfileDetail.setVisibility(View.VISIBLE);
//            mProfileDetailNext.setVisibility(View.VISIBLE);
//            if(!myPrefs.getIsAdmin()){
//                mControlCenter.setVisibility(View.GONE);
//                mControlCenterNext.setVisibility(View.GONE);
//            }
//            else{
//                mControlCenter.setVisibility(View.VISIBLE);
//                mControlCenterNext.setVisibility(View.VISIBLE);
//            }
        }
        else{
//            mAddAccountButton.show();
//            mSuggestion.setVisibility(View.VISIBLE);
//            btnSignout.setVisibility(View.GONE);
//            mProfileDetail.setVisibility(View.GONE);
//            mProfileDetailNext.setVisibility(View.GONE);
//            mControlCenter.setVisibility(View.GONE);
//            mControlCenterNext.setVisibility(View.GONE);
        }
    }

    public void updateUser(){
        Toast.makeText(getActivity(), "Chưa có chức năng này!!", Toast.LENGTH_SHORT).show();
    }

    public void DisplayUser(){

        user = mAuth.getCurrentUser();
        String userID = user.getUid();

        DocumentReference userGet = ((MainActivity)getActivity()).mDb.collection("user_info").document(userID);
        userGet.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                users =documentSnapshot.toObject(Users.class);
//
//                setValueUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "Error: " + e.getMessage());
            }
        });
    }


    @OnClick(R.id.btnBack)
    public void BackToMain(View view) {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    private void gotoLoginActivity() {
        startActivity(new Intent(getActivity(), Login.class));
        getActivity().finish();
    }

    public void ChangeAvatar(View view) {
        Toast.makeText(getActivity(), "Đây là chức năng thay đổi avt, hiện tại chức năng này chưa có", Toast.LENGTH_SHORT).show();
    }


}