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

import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CinemaManagement extends Fragment {
    public static CinemaManagement newInstance() {
        return new CinemaManagement();
    }

    @BindView(R.id.back_button)
    ImageView mBackButton;
    @BindView(R.id.title)
    TextView mTitle;

    @OnClick(R.id.back_button)
    void back() {
//        getMainActivity().dismiss();
    }


    @OnClick(R.id.see_all_cinema_panel)
    void goToAllCinemasPage() {
        ((MainActivity) getActivity()).loadFragment(AllCinemas.newInstance());
    }

    @OnClick(R.id.choose_cinema_for_showing_panel)
    void goToChooseCinemasForShowing() {
        ((MainActivity) getActivity()).loadFragment(ChooseCinema.newInstance());
    }

    @OnClick(R.id.add_new_cinema_panel)
    void gotToAddNewCinema() {
        ((MainActivity) getActivity()).loadFragment(AddNewCinema.newInstance());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
    }


    @Nullable
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.admin_cinema_dashboard,container,false);
    }
}
