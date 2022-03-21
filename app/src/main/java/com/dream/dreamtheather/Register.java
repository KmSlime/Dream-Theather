package com.dream.dreamtheather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Register extends AppCompatActivity {

    ImageButton btnEyeShow, btnDatePicker;
    EditText edtAuthor_Name, edtRegisterUsername, edtRegisterEmail, edtRegisterPassword, edtRegisterPasswordConfirm;
    Button btnConfirm;
    TextView tvDatepicked, tvAge;
    DatePickerDialog.OnDateSetListener dateSetListener1;

    // Write a message to the firebase database
    FirebaseDatabase database;
    DatabaseReference reference;

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
                edtAuthor_Name.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    public boolean checkPassConfirm(){
        if (edtRegisterPassword.getText().toString().equals(edtRegisterPasswordConfirm.getText().toString()))
        {
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                if (checkValidate()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkPassConfirm()) {
                        Intent intent = new Intent(Register.this, Login.class);
                        intent.putExtra("RegisterUser", edtRegisterUsername.getText().toString());
                        intent.putExtra("RegisterEmail", edtRegisterEmail.getText().toString());
                        intent.putExtra("RegisterPassword", edtRegisterPassword.getText().toString());
                        startActivity(intent);
                        finish();
                    }else Toast.makeText(getApplicationContext(), "Mật khẩu không trùng nhau", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}