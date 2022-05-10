package com.dream.dreamtheather.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TheatherFragment extends Fragment implements OnCompleteListener<QuerySnapshot>, OnFailureListener {

    TextView tvCinemaName;


    CinemaAdapter cinemaAdapter;

    FirebaseFirestore firebaseFirestore;

    @BindView(R.id.rv_cinema)
    RecyclerView rv_cinema;

    @Nullable
    @BindView(R.id.tvAddressCinema)
    TextView tvAddressCinema;

    @BindView(R.id.tvHotline)
    TextView tvHotline;

    @BindView(R.id.img)
    ImageView img;

    @BindView(R.id.itemCinema)
    ConstraintLayout itemCinema;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_theather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        firebaseFirestore = ((MainActivity)getActivity()).mDb;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        rv_cinema.setLayoutManager(layoutManager);

        cinemaAdapter = new CinemaAdapter(getActivity());
        rv_cinema.setAdapter(cinemaAdapter);
        refreshData();

    }

    private void refreshData() {
        firebaseFirestore.collection("cinema_list")
                .get()
                .addOnCompleteListener(this)
                .addOnFailureListener(this);
    }

    public void bind(Cinema cinema){
        tvCinemaName.setText(cinema.getName());
        tvHotline.setText(cinema.getHotline());
        tvAddressCinema.setText(cinema.getAddress());
        Glide.with(getContext()).load(cinema.getImageUrl()).error(R.drawable.yourname).into(img);
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        rv_cinema.setVisibility(View.VISIBLE);

        if (task.isSuccessful()) {
            QuerySnapshot querySnapshot = task.getResult();

            List<Cinema> cinemaList  = querySnapshot.toObjects(Cinema.class);

            if(cinemaAdapter!=null)
                cinemaAdapter.setData(cinemaList);
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d("TAG", "onFailure");
        rv_cinema.setVisibility(View.GONE);
    }
}