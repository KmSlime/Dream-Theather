package com.dream.dreamtheather.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Movie;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.adapter.SpotlightAdapter;
import com.dream.dreamtheather.adapter.SpotlightViewPagerAdapter;
import com.dream.dreamtheather.adapter.transformer.DepthPageTransformer;
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

    public SpotlightFragment newInstance(){
        SpotlightFragment fragment = new SpotlightFragment();
        return fragment;
    }

    @BindView(R.id.viewpager_spotlight)
    ViewPager2 viewPager;

    SpotlightAdapter viewPagerAdapter;

    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_spotlight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        viewPager = view.findViewById(R.id.viewpager_spotlight);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(100,0,100,0);
        firebaseFirestore = ((MainActivity) getActivity()).firebaseFirestore;
        refreshData();
        viewPager.setPageTransformer(new DepthPageTransformer());
        viewPager.registerOnPageChangeCallback(viewPagerCallback);
    }

    private int currentPosition;
    private int listSize;

    final ViewPager2.OnPageChangeCallback viewPagerCallback =
            new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    currentPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                    if (state == ViewPager2.SCROLL_STATE_IDLE || state == ViewPager2.SCROLL_STATE_DRAGGING) {
                        if (currentPosition == 0) {
                            viewPager.setCurrentItem(listSize - 2, false);
                        } else if (currentPosition == listSize - 1) {
                            viewPager.setCurrentItem(1, false);
                        }
                    } else
                    if (state == ViewPager2.SCROLL_STATE_DRAGGING
                            && currentPosition == listSize) {
                        //we scroll too fast and miss the state SCROLL_STATE_IDLE for the previous item
                        viewPager.setCurrentItem(2, false);
                    }
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

            viewPagerAdapter = new SpotlightAdapter(getContext());
            viewPagerAdapter.setData(listMovieGetFromFirebase);
            viewPager.setAdapter(viewPagerAdapter);
            listSize = viewPagerAdapter.getItemCount();
            Log.v(TAG, "done add spotlight movie");
        } else
            Log.w(TAG, "Error getting documents.", task.getException());
    }

    @Override
    public void onFailure(@NonNull Exception e) { // here
        Log.d(TAG, "onFailure");
        viewPager.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
//        handler.postDelayed(runnable, 2000); // Slide duration 3 seconds

    }
}
