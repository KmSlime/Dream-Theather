package com.dream.dreamtheather.Fragment;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.DateShowTime;
import com.dream.dreamtheather.Model.DetailShowTime;
import com.dream.dreamtheather.Model.ShowTime;
import com.dream.dreamtheather.Model.Ticket;
import com.dream.dreamtheather.Model.UserInfo;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.adapter.ChooseSeatAdapter;
import com.dream.dreamtheather.util.Tool;
import com.dream.dreamtheather.util.widget.BoundItemDecoration;
import com.dream.dreamtheather.util.widget.SuccessTickView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tuyenmonkey.mkloader.MKLoader;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseSeatBottomSheet extends BottomSheetDialogFragment
        implements ChooseSeatAdapter.OnSelectedChangedListener,
        OnCompleteListener<DocumentSnapshot>, OnFailureListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "ChooseSeatBottomSheet";
    private static final int DBS = com.google.android.material.R.id.design_bottom_sheet;

    private String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String NUM = "123456789";
    private int mPriceValue = 0;
    private List<Integer> mSelects = new ArrayList<>();

    public static ChooseSeatBottomSheet newInstance(AppCompatActivity activity, ShowTime showTime, int datePos, int timePos) {
        ChooseSeatBottomSheet c = new ChooseSeatBottomSheet();
        c.init(showTime, datePos, timePos);
        c.show(activity.getSupportFragmentManager(), TAG);
        return c;
    }

    private ShowTime mShowTime;
    private DateShowTime mDateShowTime;
    private DetailShowTime mDetailShowTime;
    private FirebaseUser mUser;
    private FirebaseFirestore mDb;


    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;
    ChooseSeatAdapter mAdapter;
    @BindView(R.id.seat_list)
    TextView mSeatList;

    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.button)
    Button mPayNow;
    @BindView(R.id.alert)
    TextView mAlert;

    private void init(ShowTime showTime, int datePos, int timePos) {
        mShowTime = showTime;
        mDateShowTime = showTime.getDateShowTime().get(datePos);
        mDetailShowTime = mDateShowTime.getDetailShowTimes().get(timePos);
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_seat_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        onViewCreated(view);
    }

    private UserInfo mUserInfo;

    private void getUserInfo() {
        String id = mUser.getUid();

        DocumentReference userGet = mDb.collection("user_info").document(id);
        userGet.get()
                .addOnSuccessListener(documentSnapshot -> {
                    mUserInfo = documentSnapshot.toObject(UserInfo.class);
                    seeHowMuchMoney();
                });

        mDb.collection("database_info")
                .document("show_time_info").get()
                .addOnCompleteListener(this)
                .addOnFailureListener(this);


    }

    private int mBalance = 0;

    private void seeHowMuchMoney() {
        mBalance = mUserInfo.getBalance();
    }

    private void onViewCreated(View view) {
        ButterKnife.bind(this, view);
        mAdapter = new ChooseSeatAdapter(getContext());
        mDb = ((MainActivity) getActivity()).firebaseFirestore;
        mUser = ((MainActivity) getActivity()).user;

        getUserInfo();

        mAdapter.setRowAndColumn(mDetailShowTime.getSeatColumnNumber(),
                                mDetailShowTime.getSeatRowNumber());
        mAdapter.setOnSelectedChangedListener(this);
        mRecyclerView.setAdapter(mAdapter);
        setLayoutManager();
        mAdapter.setData(mDetailShowTime.getSeats());
    }

    @OnClick(R.id.toggleButton)
    void dismissThis() {
        dismiss();
    }

    @Override
    public void onSelectedChanged(List<Integer> selects) {
        mSelects.clear();
        mSelects.addAll(selects);

        String text = "";
        int column = mDetailShowTime.getSeatColumnNumber();
        int row = mDetailShowTime.getSeatRowNumber();
        mPriceValue = mDetailShowTime.getPrice() * selects.size();

        for (int select : selects) {
            int myRow = select / row;
            int myColumn = select % column;
            text = String.format("%s%c%c ", text, ABC.charAt(myRow), NUM.charAt(myColumn));
        }
        mSeatList.setText(text);
        mPrice.setText(mPriceValue + " ₫");

        if (mPriceValue > mBalance) {
            mAlert.setText(getString(R.string.your_balanced_is) + mBalance + getString(R.string.it_is_not_enough));
            mAlert.setVisibility(View.VISIBLE);
            mPayNow.setTextColor(0xFF666666);
            mPayNow.setEnabled(false);
        } else if (mPriceValue == 0) {
            mAlert.setVisibility(View.GONE);
            mPayNow.setTextColor(0xFF666666);
            mPayNow.setEnabled(false);
        } else {
            mAlert.setVisibility(View.GONE);
            mPayNow.setTextColor(getResources().getColor(R.color.FlatOrange));
            mPayNow.setEnabled(true);
        }

    }

    void setLayoutManager() {
        GridLayoutManager grid = new GridLayoutManager(getContext(),
                mDetailShowTime.getSeatColumnNumber());
        float width = Tool.getScreenSize(getContext())[0] - getResources().getDimension(R.dimen.margin_start_recycler_view) - getResources().getDimension(R.dimen.margin_end_recycler_view);
        int column = mDetailShowTime.getSeatColumnNumber();
        int row = mDetailShowTime.getSeatRowNumber();
        BoundItemDecoration b = new BoundItemDecoration(width, (width / column * row), column, row, 0, 0);
        mRecyclerView.addItemDecoration(b);
        mRecyclerView.setLayoutManager(grid);
    }

    private long mNextTicketID = 0;

    @OnClick(R.id.button)
    void payNow() {
        Ticket ticket = new Ticket();

        ticket.setID((int) mNextTicketID);
        ticket.setCinemaID(mShowTime.getCinemaID());
        ticket.setMovieID(mShowTime.getMovieID());
        ticket.setCinemaName(mShowTime.getCinemaName());
        ticket.setMovieName(mShowTime.getMovieName());
        ticket.setDate(mDateShowTime.getDate());
        ticket.setTime(mDetailShowTime.getTime());
        ticket.setRoom(mDetailShowTime.getRoom());
        ticket.setSeat(mSeatList.getText().toString());
        ticket.setPrice(mPriceValue);
        ticket.setUserUID(mUser.getUid());
        mTicket = ticket;
        mSendingDialog = new BottomSheetDialog(getContext());

        mSendingDialog.setContentView(R.layout.send_new_movie);
        mSendingDialog.setCancelable(false);
        mSendingDialog.findViewById(R.id.close).setOnClickListener(v -> cancelSending());
        mSendingDialog.show();
        successStep = 4;
        saveShowTime(ticket);
        upTicketNumber(ticket);
        saveTicket(ticket);
        saveTicketToUserInfo(ticket);
    }

    private BottomSheetDialog mSendingDialog;

    boolean cancelled = false;

    void cancelSending() {
        if (mSendingDialog != null)
            mSendingDialog.dismiss();
        cancelled = true;
    }

    void setTextSending(String text, int color) {
        if (mSendingDialog != null) {
            TextView textView = mSendingDialog.findViewById(R.id.sending_text);
            if (textView != null) {

                AlphaAnimation aa = new AlphaAnimation(0, 1);
                aa.setFillAfter(true);
                aa.setDuration(500);
                textView.setText(text);
                textView.setTextColor(color);
                textView.startAnimation(aa);
            }
        }
    }

    void setOnSuccess() {
        if (cancelled) return;
        cancelled = false;
        if (mSendingDialog != null) {
            MKLoader mkLoader = mSendingDialog.findViewById(R.id.loading);
            if (mkLoader != null) mkLoader.setVisibility(View.INVISIBLE);
            SuccessTickView s = mSendingDialog.findViewById(R.id.success_tick_view);
            if (s != null) {

                s.postDelayed(() -> {
                    mSendingDialog.dismiss();
                    mRecyclerView.postDelayed(this::showTicketPrint, 350);
                }, 2000);
                s.setVisibility(View.VISIBLE);
                s.startTickAnim();
                setTextSending("You buy ticket successfully", getResources().getColor(R.color.FlatGreen));
            }
        }
    }

    void setOnFailure() {
        if (mSendingDialog != null) {
            mSendingDialog.dismiss();
            Toast.makeText(getContext(), "Cannot buy ticket, please try again!", Toast.LENGTH_SHORT);
        }
    }

    void showTicketPrint() {
        dismiss();
        ((MainActivity) getActivity()).loadFragment(PurchaseScreen.newInstance(mTicket, mUserInfo));
    }


    private int successStep;
    Ticket mTicket;

    private void upTicketNumber(Ticket t) {
        mNextTicketID++;
        mDb.collection("database_info")
                .document("show_time_info")
                .update("ticket_count", mNextTicketID)
                .addOnCompleteListener(task -> checkSuccess())
                .addOnFailureListener(e -> fail());
    }

    private void fail() {
        cancelled = true;
        setOnFailure();
    }

    private void saveShowTime(Ticket t) {
        List<Boolean> list = mDetailShowTime.getSeats();
        for (int i : mSelects) {
            list.set(i, true);
        }

        mDb.collection("show_time")
                .document(mShowTime.getID() + "")
                .set(mShowTime).addOnSuccessListener(aVoid -> checkSuccess())
                .addOnFailureListener(e -> fail());
    }

    private void saveTicket(Ticket t) {
        mDb.collection("ticket")
                .document(t.getID() + "")
                .set(t).addOnSuccessListener(aVoid -> checkSuccess())
                .addOnFailureListener(e -> fail());
    }

    private void checkSuccess() {
        successStep--;
        Log.d(TAG, "checkSuccess: step = " + successStep);
        if (successStep == 0) {
            Log.d(TAG, "checkSuccess ; success");
            setOnSuccess();
        }
    }

    private void saveTicketToUserInfo(Ticket t) {
        mUserInfo.getIdTicket().add(t.getID());
        mUserInfo.setBalance(mBalance - mPriceValue);
        mDb.collection("user_info")
                .document(mUser.getUid()).set(mUserInfo)
                .addOnSuccessListener(aVoid -> checkSuccess())
                .addOnFailureListener(e -> fail());
    }

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        DocumentSnapshot s = task.getResult();
        if (s != null)
            mNextTicketID = s.getLong("ticket_count");

    }

    @Override
    public void onFailure(@NonNull Exception e) {

    }

    @Override
    public void onGlobalLayout() {
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        FrameLayout bottomSheet = (FrameLayout) dialog.findViewById(DBS);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setPeekHeight(-Tool.getNavigationHeight(requireActivity()));
        behavior.setHideable(false);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == STATE_COLLAPSED)
                    ChooseSeatBottomSheet.this.dismiss();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }
}
