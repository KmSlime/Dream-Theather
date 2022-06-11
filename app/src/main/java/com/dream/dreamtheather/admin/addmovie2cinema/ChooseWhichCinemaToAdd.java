package com.dream.dreamtheather.admin.addmovie2cinema;

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

import com.dream.dreamtheather.AdminActivity;
import com.dream.dreamtheather.Model.Cinema;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.adapter.CinemaAdapter;
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
import butterknife.OnClick;

public class ChooseWhichCinemaToAdd extends Fragment implements OnCompleteListener<QuerySnapshot>, OnFailureListener, CinemaAdapter.CinemaOnClickListener {
    private static final String TAG = "ChooseWhichCinema";

    public static ChooseWhichCinemaToAdd newInstance() {
        return new ChooseWhichCinemaToAdd();
    }

    @BindView(R.id.back_button)
    ImageView mBackButton;

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.textView)
    TextView mErrorTextView;

    CinemaAdapter mAdapter;

    FirebaseFirestore db;


    @OnClick(R.id.back_button)
    void back() {
//        getMainActivity().dismiss();
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_add_movie2cinema, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        db = FirebaseFirestore.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new CinemaAdapter(getActivity());

        mRecyclerView.setAdapter(mAdapter);

        setSwipeOptionForRecyclerView();


        swipeLayout.setOnRefreshListener(this::refreshData);
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null)
            mAdapter.setListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null)
            mAdapter.removeListener();
    }


    private void setSwipeOptionForRecyclerView() {

    }

    public void refreshData() {
        swipeLayout.setRefreshing(true);
        db.collection("cinema_list")
                .get()
                .addOnCompleteListener(this)
                .addOnFailureListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {

        if (swipeLayout.isRefreshing())
            swipeLayout.setRefreshing(false);

        mErrorTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        if (task.isSuccessful()) {
            QuerySnapshot querySnapshot = task.getResult();

            List<Cinema> mM = querySnapshot.toObjects(Cinema.class);
            Collections.sort(mM, new Comparator<Cinema>() {
                @Override
                public int compare(Cinema o1, Cinema o2) {
                    return o1.getId() - o2.getId();
                }
            });
            if (mAdapter != null)
                mAdapter.setData(mM);

        } else
            Log.w(TAG, "Error getting documents.", task.getException());
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d(TAG, "onFailure");
        if (swipeLayout.isRefreshing())
            swipeLayout.setRefreshing(false);

        mRecyclerView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(Cinema cinema) {
        ((AdminActivity) getActivity()).loadFragment(ChooseWhichMovieToAdd.newInstance(cinema));
    }
}
