package com.dream.dreamtheather.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dream.dreamtheather.R;
import com.dream.dreamtheather.Model.Movie;
import com.dream.dreamtheather.adapter.NowShowingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AllMovie extends Fragment implements OnCompleteListener<QuerySnapshot>, OnFailureListener {
    private static final String TAG = "AllMovie";

    public static AllMovie newInstance() {
        return new AllMovie();
    }

    @BindView(R.id.back_button)
    ImageView mBackButton;

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;

    @BindView(R.id.textView)
    TextView mErrorTextView;

    NowShowingAdapter mAdapter;

    FirebaseFirestore firebaseFirestore;

    @OnClick(R.id.back_button)
    void back() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_all_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new NowShowingAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mSwipeLayout.setOnRefreshListener(this::refreshData);
        refreshData();
    }

    public void refreshData() {
        mSwipeLayout.setRefreshing(true);
        firebaseFirestore
                .collection("movie")
                .get()
                .addOnCompleteListener(this)
                .addOnFailureListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {

        if (mSwipeLayout.isRefreshing())
            mSwipeLayout.setRefreshing(false);

        mErrorTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        if (task.isSuccessful()) {
            QuerySnapshot querySnapshot = task.getResult();

            List<Movie> mM = querySnapshot.toObjects(Movie.class);
            Collections.sort(mM, (o1, o2) -> o1.getId() - o2.getId());
            if (mAdapter != null)
                mAdapter.setData(mM);

        } else
            Log.w(TAG, "Error getting documents.", task.getException());
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d(TAG, "onFailure");
        if (mSwipeLayout.isRefreshing())
            mSwipeLayout.setRefreshing(false);

        mRecyclerView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

}
