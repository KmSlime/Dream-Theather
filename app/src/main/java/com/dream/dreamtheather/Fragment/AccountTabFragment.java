package com.dream.dreamtheather.Fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.dream.dreamtheather.Login;
import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Users;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.data.MyPrefs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountTabFragment extends Fragment {

    private static final String TAG = "AccountTab";
    // request code for get image choose
    private final int PICK_IMAGE_REQUEST = 22;
    private boolean isChangeValue = false;
    MyPrefs myPrefs;

    FragmentActivity mActivity;
    Context context;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage storage;
    StorageReference storageReference;

    public static GoogleSignInClient mGoogleSignInClient;

    Users user_info, curUser;

    Uri filePath;

    String avaURL;

    public static AccountTabFragment newInstance() {
        AccountTabFragment fragment = new AccountTabFragment();
        fragment.mActivity = fragment.getActivity();
        return fragment;
    }

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.imgAvata)
    RoundedImageView imgAvatar;
    @BindView(R.id.btnUploadAvatar)
    FloatingActionButton btnUploadAvatar;
    @BindView(R.id.tvBalance)
    TextView tvBalance;
    @BindView(R.id.tvLoyaltyPoint)
    TextView tvLoyaltyPoint;
    @BindView(R.id.edtAddress)
    EditText edtAddress;
    @BindView(R.id.edtPhoneNum)
    EditText edtPhoneNum;
    @BindView(R.id.edtBirthDay)
    EditText edtBirthDay;
    @BindView(R.id.btnDatePicker)
    ImageButton btnDatePicker;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtUserFullName)
    EditText edtUserFullName;
    @BindView(R.id.btnSignOut)
    Button btnSignOut;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.rad_group_gender)
    RadioGroup rad_group_gender;
    @BindView(R.id.radioFemale)
    RadioButton radioFemale;
    @BindView(R.id.radioMale)
    RadioButton radioMale;
    RadioButton radioButtonGender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        context = getContext();
//        myPrefs = new MyPrefs(context);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getUserInfo();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserInfo();
    }

    @OnClick(R.id.btnSignOut)
    void signOut() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account != null)
        {
            googleSignOut();
        }
        firebaseAuth.signOut();
//        myPrefs.setIsSignIn(false);
//        myPrefs.setIsAdmin(false);
        Log.v(TAG, "Sign Out Successful");
        gotoLoginActivity();
    }

    void googleSignOut() {
        mGoogleSignInClient.asGoogleApiClient()
                .stopAutoManage(mActivity);
        mGoogleSignInClient
                .signOut()
                .addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            sendMessage("Đăng xuất thành công");
                            gotoLoginActivity();
                        } else {
                            sendMessage("Có lỗi khi đăng xuất");
                        }
                    })
                .addOnFailureListener(e -> sendMessage(e.getMessage()));
    }

    void sendMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.btnBack)
    public void BackToMain(View view) {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @OnClick({R.id.btnUploadAvatar, R.id.imgAvata})
    public void ChangeAvatar(View view) {
        SelectImage();
    }

    @OnClick(R.id.btnSave)
    public void updateUser(View view) {
        checkValueChangeForUserInfo();
        if (isChangeValue) {
            sendUserInfo(user_info);
        }

    }

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    String dateOfBirth;
    private final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            month = month + 1;
            dateOfBirth = day + "/" + month + "/" + year;
            edtBirthDay.setText(dateOfBirth);
            Log.d(TAG, "after select date:" + dateOfBirth);
        }
    };
    ;

    @OnClick(R.id.btnDatePicker)
    public void pickBirthDay(View view) {
        Calendar calendar = Calendar.getInstance();
        Date userDoB = new Date();
        try {
            if(user_info.getBirthDay() != "")
                userDoB = format.parse(user_info.getBirthDay());
            if (userDoB != null) {
                calendar.setTime(userDoB);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //open dialog with exactly user's birth day
        btnDatePicker.setOnClickListener(this::pickBirthDay);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                android.R.style.Theme_Holo_Dialog_MinWidth,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private void getUserInfo() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser = firebaseAuth.getCurrentUser();
            String userID = firebaseUser.getUid();
            DocumentReference userGet = firebaseFirestore
                    .collection("user_info")
                    .document(userID);

            userGet.get().addOnSuccessListener(documentSnapshot -> {
                user_info = documentSnapshot.toObject(Users.class);
                curUser = user_info;
                displayUserInfo(user_info);
            }).addOnFailureListener(e -> Log.e(TAG, "Error: failed to get user info with ID: " + userID + " \nwith Error" + e.getMessage()));
        } else {
            edtUserFullName.setText("@anonymous");
            Glide.with(this)
                    .load(R.drawable.default_avatar)
                    .into(imgAvatar);
        }
    }

    private void displayUserInfo(Users users) {
        String avt = users.getAvaUrl();

        Glide.with(getContext())
                .load(avt)
                .error(R.drawable.default_avatar)
                .placeholder(R.drawable.movie_boy)
                .override(120, 150)
                .into(imgAvatar);

        tvBalance.setText(String.valueOf(users.getBalance()));
        tvLoyaltyPoint.setText(String.valueOf(users.getLoyaltyPoint()));

        edtUserFullName.setText(users.getFullName());
        edtAddress.setText(users.getAddress());
        edtPhoneNum.setText(users.getPhoneNumber());
        edtBirthDay.setText(users.getBirthDay());
        edtEmail.setText(users.getEmail());

        String gender = users.getGender().trim().toLowerCase();

        if (gender.equals(radioMale.getText().toString().toLowerCase())) {
            radioMale.toggle();
        } else if (gender.equals(radioFemale.getText().toString().toLowerCase())) {
            radioFemale.toggle();
        }

    }

    private void gotoLoginActivity() {
        startActivity(new Intent(mActivity, Login.class));
        mActivity.finish();
    }

    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickedImageLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> pickedImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d(TAG, "requestCode: ");
                    Log.d(TAG, "resultCode: " + result.getResultCode());
                    filePath = result.getData().getData();
                    changeLocalAvatar();
                }
            }
    );

    private void changeLocalAvatar() {
        if (filePath != null) {
            Picasso.get()
                    .load(filePath.toString())
                    .config(Bitmap.Config.RGB_565)
                    .resize(120, 150)
                    .into(imgAvatar);
        }
        uploadImage();
    }

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            File file = new File(filePath.getPath());

            StorageReference ref = storageReference.
                    child("user-avatar/" + file.getName());

            ref.putFile(filePath)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),
                                        "Tải ảnh lên thành công!!", Toast.LENGTH_SHORT)
                                        .show();

                                ref.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            this.avaURL = uri.toString();
                                            Log.v(TAG, "Success choose: " + avaURL);
                                            updateUserInfoAvatarUrl(avaURL);
                                        });
                            })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),
                                "Failed " + e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        Log.v(TAG, e.getMessage());
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            });
        }
    }

    private void updateUserInfoAvatarUrl(String avaURL) {
        firebaseFirestore
                .collection("user_info")
                .document(firebaseUser.getUid())
                .update("avaUrl", avaURL);
        Toast.makeText(getContext(),
                "Cập nhật ảnh thành công!!", Toast.LENGTH_SHORT)
                .show();
        Toast.makeText(getContext(),
                "Có thể sẽ mất vài phút để cập nhật ở tất cả nền tảng!!", Toast.LENGTH_SHORT)
                .show();
    }

    private void checkValueChangeForUserInfo() {
        String fullName = edtUserFullName.getText().toString().trim();
        String dob = edtBirthDay.getText().toString().trim();
        String phoneNum = edtPhoneNum.getText().toString().trim();

        int radioButtonId = rad_group_gender.getCheckedRadioButtonId();
        radioButtonGender = getActivity().findViewById(radioButtonId);
        String gender = "Nam";
        if (radioButtonGender != null) {
            gender = radioButtonGender.getText().toString();
        }
        String address = edtAddress.getText().toString().trim();
        if (!curUser.getFullName().equals(fullName)) {
            Log.v(TAG, "name     | old:" + user_info.getFullName() + "- new: " + fullName);
            user_info.setFullName(fullName);
            isChangeValue = true;
        }
        if (!curUser.getBirthDay().equalsIgnoreCase(dob)) {
            Log.v(TAG, "dob      | old:" + user_info.getBirthDay() + "- new: " + dob);
            user_info.setBirthDay(dob);
            isChangeValue = true;
        }
        if (!curUser.getGender().equals(gender)) {
            Log.v(TAG, "gender   | old:" + user_info.getGender() + "- new: " + gender);
            user_info.setGender(gender);
            isChangeValue = true;
        }
        if (!curUser.getPhoneNumber().equals(phoneNum)) {
            Log.v(TAG, "phone    | old:" + user_info.getPhoneNumber() + "- new: " + phoneNum);
            user_info.setPhoneNumber(phoneNum);
            isChangeValue = true;
        }
        if (!curUser.getAddress().equals(address)) {
            Log.v(TAG, "address  | old:" + user_info.getAddress() + "- new: " + address);
            user_info.setAddress(address);
            isChangeValue = true;
        }
    }

    private void sendUserInfo(Users info) {
        firebaseFirestore.collection("user_info")
                .document(firebaseUser.getUid())
                .set(info)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),
                            "Cập nhật thông tin thành công!",
                            Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "addUserToDatabase:success");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Cập nhật thông tin không thành công!",
                            Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "addUserToDatabase:failure", e);
                });
    }
}