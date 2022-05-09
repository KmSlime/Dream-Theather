package com.dream.dreamtheather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dream.dreamtheather.Fragment.AccountTabFragment;
import com.dream.dreamtheather.Fragment.BookingFragment;
import com.dream.dreamtheather.Fragment.HomeTabFragment;
import com.dream.dreamtheather.Fragment.TheatherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final int HOME = R.id.navigation_home,
            FAV = R.id.navigation_favorites,
            ACCOUNT = R.id.navigation_account ;

    public FirebaseFirestore mDb;

    private BottomNavigationView bottom_navigation;
    private ActionBar toolBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = getSupportActionBar();

        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        mDb = FirebaseFirestore.getInstance();

    }

    private final NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                Fragment fragment;
                switch (item.getItemId()) {
                    case HOME :
                        fragment = new HomeTabFragment();
                        loadFragment(fragment);
                        return true;
                    case FAV :
                        fragment = new TheatherFragment();
                        loadFragment(fragment);
                        return true;
                    case ACCOUNT :
                        fragment = new AccountTabFragment();
                        loadFragment(fragment);
                        return true;
                }
                return false;
            };

    public void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.FragmentHomeTab, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }
}