package com.dream.dreamtheather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dream.dreamtheather.Model.InputValidatorHelper;
import com.dream.dreamtheather.Model.UserHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Register extends AppCompatActivity {

    private ImageButton btnEyeShow, btnDatePicker;
    private EditText edtAuthor_Name, edtRegisterUsername, edtRegisterEmail, edtRegisterPassword, edtRegisterPasswordConfirm, edtPhoneNum;
    private Button btnConfirm;
    private TextView tvDatepicked, tvAge;
    private DatePickerDialog.OnDateSetListener dateSetListener1;
    private boolean pickdate = false;
    private ProgressBar progressBar;

    // Write a message to the firebase database
    private FirebaseDatabase database;
    private DatabaseReference reference;

    // Firebase
    FirebaseAuth firebaseAuth;


    //validation checker
    InputValidatorHelper inputValidatorHelper = new InputValidatorHelper();

    //
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //textview
        tvAge = findViewById(R.id.tvAge);
        tvDatepicked = findViewById(R.id.tvDatepicked);

        //button + img button
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnEyeShow = findViewById(R.id.btnEyeShow);
        btnConfirm = findViewById(R.id.btnConfirm);

        //editText
        edtAuthor_Name = findViewById(R.id.edtAuthor_Name);
        edtRegisterUsername = findViewById(R.id.edtRegisterUsername);
        edtRegisterEmail = findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        edtRegisterPasswordConfirm = findViewById(R.id.edtRegisterPasswordConfirm);
        edtPhoneNum = findViewById(R.id.edtPhoneNum);

        //Progress Bar
        progressBar = findViewById(R.id.progressBar);



        btnEyeShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        edtRegisterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                    case MotionEvent.ACTION_UP:
                        edtRegisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        return true;
                }
                return false;
            }
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Register.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                        dateSetListener1, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        //current days
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        tvDatepicked.setText(String.valueOf(simpleDateFormat.format(Calendar.getInstance().getTime())));

        //date of birth
        dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String dateOfBirth = day + "/" + month + "/" + year;
                tvDatepicked.setText(dateOfBirth);

                //cal Age
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    //parse thành ngày
                    Date birth = simpleDateFormat.parse(dateOfBirth);
                    Date currentDay = simpleDateFormat.parse(String.valueOf(simpleDateFormat.format(Calendar.getInstance().getTime())));

                    //parse sang số
                    long startDate = birth.getTime();
                    long endDate = currentDay.getTime();

                    if (startDate <= endDate) {
                        Period period = new Period(startDate, endDate, PeriodType.yearMonthDay());
                        int years = period.getYears();
                        int months = period.getMonths();
                        int days = period.getDays();
                        tvAge.setText("Tuổi: ");
                        tvDatepicked.setText(years + " tuổi " + months + " tháng " + days + " ngày");
                        pickdate = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Ngày sinh phải trước ngày hiện tại!", Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public boolean checkValidate(){
        if (edtAuthor_Name.getText().toString().isEmpty() ||
                edtRegisterPassword.getText().toString().isEmpty() ||
                edtRegisterEmail.getText().toString().isEmpty() ||
                edtRegisterPasswordConfirm.getText().toString().isEmpty() ||
                edtAuthor_Name.getText().toString().isEmpty() ||
                edtPhoneNum.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    public boolean emailValidate(){
        if(inputValidatorHelper.isValidEmail(edtRegisterEmail.getText().toString())){
            return true;
        } else{
            edtRegisterEmail.setError("Nhập sai định dạng email");
            edtRegisterEmail.requestFocus();
            return false;
        }
    }

    public boolean checkPassConfirm(){
        if(edtRegisterPassword.getText().length() < 6){
            edtRegisterPasswordConfirm.setError("Mật khẩu không được dưới 6 ký tự");
            edtRegisterPasswordConfirm.requestFocus();
            return false;
        }else if (edtRegisterPassword.getText().toString().equals(edtRegisterPasswordConfirm.getText().toString()))
        {
            return true;
        } else
        {
            edtRegisterPasswordConfirm.setError("Mật khẩu không trùng nhau");
            edtRegisterPasswordConfirm.requestFocus();
            return false;
        }
    }

    public boolean checkPhoneNum(){
        if((edtPhoneNum.getText().length()>=8 && edtPhoneNum.getText().length()<=10))
        {
            return true;
        } else
        {
            edtPhoneNum.setError("SĐT phải từ 8 đến 10 số!");
            edtPhoneNum.requestFocus();
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidate()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkPassConfirm()) {
                        if(emailValidate()){
                            if(checkPhoneNum()){
                                if(pickdate) {
                                    //save data in firebase on button click
                                    database = FirebaseDatabase.getInstance();
                                    firebaseAuth = FirebaseAuth.getInstance();
                                    reference = database.getReference("Users/");

                                    //get all values
                                    String email = edtRegisterEmail.getText().toString().trim();
                                    String userName = edtRegisterUsername.getText().toString().trim();
                                    String passWord = edtRegisterPassword.getText().toString().trim();
                                    String phoneNum = edtPhoneNum.getText().toString().trim();
                                    String dateOfBirth = tvDatepicked.getText().toString().trim();

                                    UserHelperClass userHelperClass = new UserHelperClass(email, userName, passWord, phoneNum, dateOfBirth, "", "", "", 0);
                                    //add a user with key as email to firebase
                                    reference.child(phoneNum).setValue(userHelperClass);

                                    firebaseAuth.createUserWithEmailAndPassword(email, passWord)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()) {
                                                //send noti to email
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                userID = user.getUid();

                                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(Register.this, "Đã gửi email xác nhận", Toast.LENGTH_LONG).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("Thất bại:", "Không gửi được email" + e.getMessage());
                                                    }
                                                });

                                                Toast.makeText(Register.this, "Tạo tài khoản thành công", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(Register.this, UserProfile.class);
                                                intent.putExtra("RegisterUser", edtRegisterUsername.getText().toString());
                                                intent.putExtra("RegisterEmail", edtRegisterEmail.getText().toString());
                                                intent.putExtra("RegisterPassword", edtRegisterPassword.getText().toString());
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(Register.this, "Có gì đó không ổn", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                } else Toast.makeText(getApplicationContext(), "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                progressBar.setVisibility(View.VISIBLE);
            }

        });

    }

    public void BackToLogin(View view) {
        startActivity(new Intent(Register.this, Login.class));
        finish();
    }
}