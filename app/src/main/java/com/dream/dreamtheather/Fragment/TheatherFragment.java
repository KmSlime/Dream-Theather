package com.dream.dreamtheather.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Cinema;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.adapter.CinemaAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TheatherFragment extends Fragment implements OnCompleteListener<QuerySnapshot>, OnFailureListener {

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.errorTextView)
    TextView errorTextView;

    @BindView(R.id.rv_cinema)
    RecyclerView rv_cinema;

    CinemaAdapter cinemaAdapter;

    FirebaseFirestore firebaseFirestore;

    public static TheatherFragment newInstance() {
        TheatherFragment fragment = new TheatherFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_theather,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("TAG", "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        firebaseFirestore = ((MainActivity)getActivity()).firebaseFirestore;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        rv_cinema.setLayoutManager(layoutManager);

        cinemaAdapter = new CinemaAdapter(getActivity());
        rv_cinema.setAdapter(cinemaAdapter);
        swipeLayout.setOnRefreshListener(this::refreshData);
        refreshData();
    }

    public void refreshData() {
        swipeLayout.setRefreshing(true);
        firebaseFirestore.collection("showing_cinema")
                .get()
                .addOnCompleteListener(this)
                .addOnFailureListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {

        if(swipeLayout.isRefreshing())
            swipeLayout.setRefreshing(false);

        errorTextView.setVisibility(View.GONE);
        rv_cinema.setVisibility(View.VISIBLE);

        if (task.isSuccessful()) {
            QuerySnapshot querySnapshot = task.getResult();

            List<Cinema> mM = querySnapshot.toObjects(Cinema.class);
            Collections.sort(mM, new Comparator<Cinema>() {
                @Override
                public int compare(Cinema o1, Cinema o2) {
                    return o1.getId() - o2.getId();
                }});
            if(cinemaAdapter!=null)
                cinemaAdapter.setData(mM);

        } else
            Log.w("TAG", "Error getting documents.", task.getException());
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d("TAG", "onFailure");
        if(swipeLayout.isRefreshing())
            swipeLayout.setRefreshing(false);

        rv_cinema.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
    }
}