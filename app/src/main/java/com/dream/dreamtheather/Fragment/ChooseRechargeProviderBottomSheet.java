package com.dream.dreamtheather.Fragment;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Ticket;
import com.dream.dreamtheather.Model.UserInfo;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.RechargeActivity;
import com.dream.dreamtheather.adapter.ChooseSeatAdapter;
import com.dream.dreamtheather.util.Tool;
import com.dream.dreamtheather.util.widget.SuccessTickView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseRechargeProviderBottomSheet extends BottomSheetDialogFragment
        implements ChooseSeatAdapter.OnSelectedChangedListener,
        OnCompleteListener<DocumentSnapshot>, OnFailureListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "ChooseRechargeProviderBottomSheet";
    private static final int DBS = com.google.android.material.R.id.design_bottom_sheet;

    private String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String NUM = "123456789";
    private int mPriceValue = 0;
    private List<Integer> mSelects = new ArrayList<>();

    public static ChooseRechargeProviderBottomSheet newInstance(AppCompatActivity activity) {
        ChooseRechargeProviderBottomSheet c = new ChooseRechargeProviderBottomSheet();
        c.show(activity.getSupportFragmentManager(), TAG);
        return c;
    }

    private FirebaseUser mUser;
    private FirebaseFirestore firebaseFirestore;

    @BindView(R.id.rad_group_provider)
    RadioGroup rad_group_provider;
    @BindView(R.id.tvAmountRecharge)
    EditText tvAmountRecharge;
    @BindView(R.id.button)
    Button mPayNow;
    @BindView(R.id.alert)
    TextView mAlert;

    @BindView(R.id.providerZalo)
    RadioButton providerZalo;
    @BindView(R.id.providerMomo)
    RadioButton providerMomo;
    @BindView(R.id.providerPaypal)
    RadioButton providerPaypal;

    RadioButton rechargeProvider;

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_recharge_provider, container, false);
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

        DocumentReference userGet = firebaseFirestore.collection("user_info").document(id);
        userGet.get()
                .addOnSuccessListener(documentSnapshot -> {
                    mUserInfo = documentSnapshot.toObject(UserInfo.class);
                    seeHowMuchMoney();
                });

        firebaseFirestore.collection("database_info")
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
        firebaseFirestore = ((MainActivity) getActivity()).firebaseFirestore;
        mUser = ((MainActivity) getActivity()).user;
        getUserInfo();
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


    @OnClick(R.id.button)
    public void rechargeNow(View view) {
        int providerChecked = rad_group_provider.getCheckedRadioButtonId();
        rechargeProvider = requireView().findViewById(providerChecked);
        String providerName = rechargeProvider.getText().toString();
        Log.d(TAG, "rechargeNow: provider checked: " + providerName );
        String amount = tvAmountRecharge.getText().toString();
        Log.d(TAG, "rechargeNow: amount recharge: "+ amount);
        mSendingDialog = new BottomSheetDialog(requireContext());

        mSendingDialog.setContentView(R.layout.send_new_recharge_order);
        mSendingDialog.setCancelable(false);
        mSendingDialog.findViewById(R.id.close).setOnClickListener(v -> cancelSending());
        mSendingDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(()->{
            switch (providerName)
            {
                case "ZaloPay":
                    startRechargeActivity(providerName,mUserInfo.getId(),amount);
                case "PayPal":
                    startRechargeActivity(providerName,mUserInfo.getId(),amount);
                case "Momo":
                    startRechargeActivity(providerName,mUserInfo.getId(),amount);
                default:
                    startRechargeActivity(providerName,mUserInfo.getId(),amount);
            }
        }, 500);


    }
    ActivityResultLauncher<Intent> rechargeIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d(TAG, "requestCode: ");
                    Log.d(TAG, "resultCode: " + result.getResultCode());
                }
            }
    );

    public void startRechargeActivity(String providerName, String userID, String amount){
        Intent intent = new Intent(requireContext(),RechargeActivity.class);
        Bundle data = new Bundle();
        data.putStringArray("data",new String[]{providerName, userID, amount});
        intent.putExtras(data);
        startActivity(intent);
//        rechargeIntentLauncher.launch(intent);
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
                }, 2000);
                s.setVisibility(View.VISIBLE);
                s.startTickAnim();
                setTextSending("Đặt mua vé thành công", getResources().getColor(R.color.FlatGreen, getActivity().getTheme()));
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

    private void fail() {
        cancelled = true;
        setOnFailure();
    }

    private void saveShowTime(Ticket t) {
//        List<Boolean> list = mDetailShowTime.getSeats();
//        for (int i : mSelects) {
//            list.set(i, true);
//        }
//
//        firebaseFirestore.collection("show_time")
//                .document(mShowTime.getID() + "")
//                .set(mShowTime).addOnSuccessListener(aVoid -> checkSuccess())
//                .addOnFailureListener(e -> fail());
    }

    private void saveTicket(Ticket t) {
        firebaseFirestore.collection("ticket")
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
        firebaseFirestore.collection("user_info")
                .document(mUser.getUid()).set(mUserInfo)
                .addOnSuccessListener(aVoid -> checkSuccess())
                .addOnFailureListener(e -> fail());
    }

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        DocumentSnapshot s = task.getResult();


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
                    ChooseRechargeProviderBottomSheet.this.dismiss();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }
}
