package com.dream.dreamtheather.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dream.dreamtheather.AdminActivity;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.admin.addmovie2cinema.ChooseWhichCinemaToAdd;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashBoard extends Fragment {
    public static DashBoard newInstance() {
        return new DashBoard();
    }

    @BindView(R.id.back_button)
    ImageView mBackButton;
    @BindView(R.id.title)
    TextView mTitle;

    @OnClick(R.id.back_button)
    void back() {

    }

    @OnClick(R.id.movie_panel)
    void goToMovieManagement() {
        ((AdminActivity) getActivity()).loadFragment(MovieManagement.newInstance());
    }

    @OnClick(R.id.cinema_panel)
    void goToCinemaManagement() {
        ((AdminActivity) getActivity()).loadFragment(CinemaManagement.newInstance());
    }

    @OnClick(R.id.add_new_showtime_panel)
    void goToChooseWhicCinemaToAdd() {
        ((AdminActivity) getActivity()).loadFragment(ChooseWhichCinemaToAdd.newInstance());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_dash_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

}
