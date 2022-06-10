package com.dream.dreamtheather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dream.dreamtheather.admin.CinemaManagement;
import com.dream.dreamtheather.admin.MovieManagement;
import com.dream.dreamtheather.admin.addmovie2cinema.ChooseWhichCinemaToAdd;

import butterknife.BindView;
import butterknife.OnClick;

public class AdminActivity extends AppCompatActivity {


    @BindView(R.id.back_button)
    ImageView mBackButton;
    @BindView(R.id.title)
    TextView mTitle;

    @OnClick(R.id.back_button)
    void back() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.AdminFragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}