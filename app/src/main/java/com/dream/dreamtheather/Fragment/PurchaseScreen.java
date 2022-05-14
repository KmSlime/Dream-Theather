package com.dream.dreamtheather.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Ticket;
import com.dream.dreamtheather.Model.UserInfo;
import com.dream.dreamtheather.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PurchaseScreen extends Fragment {

    Ticket mTicket;
    FirebaseAuth mAuth;
    UserInfo user;

    public static PurchaseScreen newInstance(Ticket t, UserInfo user) {
        PurchaseScreen p = new PurchaseScreen();
        p.mTicket = t;
        p.user = user;
        return p;
    }
    @OnClick(R.id.back_button)
    void back() {
        ((MainActivity) getActivity()).restartHomeScreen();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ticket_print,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        setContent();
    }

    @BindView(R.id.user) TextView mUser;
    @BindView(R.id.movie) TextView mMovie;
    @BindView(R.id.cinema) TextView mCinema;
    @BindView(R.id.room) TextView mRoom;
    @BindView(R.id.seat) TextView mSeat;
    @BindView(R.id.date) TextView mDate;
    @BindView(R.id.time) TextView mTime;
    @BindView(R.id.price) TextView mPrice;
    void setContent() {
        String fullName = user.getFullName();
        Log.v("Purchase","/fullName: "+ fullName);
        mUser.setText(fullName);
        mMovie.setText(mTicket.getMovieName());
        mCinema.setText(mTicket.getCinemaName());
        mRoom.setText(mTicket.getRoom()+"");
        mSeat.setText(mTicket.getSeat());
        mDate.setText(mTicket.getDate());
        mTime.setText(mTicket.getTime());
        mPrice.setText(mTicket.getPrice()+"");
    }
}
