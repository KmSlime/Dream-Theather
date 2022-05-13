package com.dream.dreamtheather.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Movie;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.adapter.SpotlightViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpotlightFragment extends Fragment implements OnCompleteListener<QuerySnapshot>, OnFailureListener {

    private static final String TAG = "SpotlightTab";
    private static final int DELAY_TIME = 3000;


    @BindView(R.id.pager_spotlight)
    ViewPager2 viewPager;

    SpotlightViewPagerAdapter viewPagerAdapter;

    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spotlight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        viewPager = view.findViewById(R.id.pager_spotlight);
        firebaseFirestore = ((MainActivity) getActivity()).firebaseFirestore;
        refreshData();
        infinityLoopViewPager();
    }

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        }
    };

    private void infinityLoopViewPager() {

        viewPager.registerOnPageChangeCallback(viewPagerCallback);
    }

    final ViewPager2.OnPageChangeCallback viewPagerCallback =
            new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    handler.removeMessages(0);
                    int itemCount = viewPager.getAdapter().getItemCount();
                    int finalPosition = position;
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem((finalPosition + 1), true);
                        }
                    };

                    if(position < itemCount){
                        Log.v(TAG,"viewpager spot: position"+ position);
                        handler.postDelayed(runnable, DELAY_TIME);
                    }
                    else {
                        Log.v(TAG,"end viewpager spot position: position"+ position);
                        handler.removeMessages(0);
                        position = -1;
                        handler.postDelayed(runnable, DELAY_TIME);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                    /**
                     * The user swiped forward or back and we need to
                     * invalidate the previous handler.
                     */
                    if (state == ViewPager2.SCROLL_STATE_DRAGGING)
                        handler.removeMessages(0);
                }
            };

    public void refreshData() {
        firebaseFirestore.collection("feature_movie")
                .get()
                .addOnCompleteListener(this)
                .addOnFailureListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {

        if (task.isSuccessful()) {
            QuerySnapshot querySnapshot = task.getResult();

            List<Movie> listMovieGetFromFirebase = querySnapshot.toObjects(Movie.class);

            Collections.sort(listMovieGetFromFirebase, new Comparator<Movie>() {
                @Override
                public int compare(Movie m1, Movie m2) {
                    return (int) (m2.getRate() - m1.getRate());
                }
            });

            viewPagerAdapter = new SpotlightViewPagerAdapter(this, listMovieGetFromFirebase);
            viewPager.setAdapter(viewPagerAdapter);
            Log.v(TAG,"done add spotlight movie");
        } else
            Log.w(TAG, "Error getting documents.", task.getException());
    }

    @Override
    public void onFailure(@NonNull Exception e) { // here
        Log.d(TAG, "onFailure");
        viewPager.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000); // Slide duration 3 seconds
    }
}
