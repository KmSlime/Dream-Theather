package com.dream.dreamtheather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.dream.dreamtheather.Fragment.AccountTabFragment;
import com.dream.dreamtheather.Fragment.HomeTabFragment;
import com.dream.dreamtheather.Fragment.TheatherFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int HOME = R.id.navigation_home,
            THEATHER = R.id.navigation_theather,
            ACCOUNT = R.id.navigation_account ;


    public FirebaseFirestore firebaseFirestore;
    public static FirebaseUser user;
    public FirebaseAuth mAuth;
    public static GoogleSignInClient mGoogleSignInClient;

    BottomNavigationView bottom_navigation;

    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        mainLayout = findViewById(R.id.mainLayout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    private final NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                Fragment fragment;
                switch (item.getItemId()) {
                    case HOME :
                        fragment = new HomeTabFragment();
                        loadFragment(fragment);
                        return true;
                    case THEATHER :
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
        transaction.replace(R.id.MainFragmentContainer, fragment);
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
        mAuth.signOut();
    }

    public void restartHomeScreen() {
        Intent intent = new Intent(this,MainActivity.class);
        finish();
        startActivity(intent);
    }

}