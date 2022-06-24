package com.dream.dreamtheather;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dream.dreamtheather.helper.AppInfo;
import com.dream.dreamtheather.helper.CreateOrder;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.interfaces.MerchantService;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class RechargeActivity extends AppCompatActivity {

    public static final String TAG = "RechargeActivity";
    public static final String schema = "demozpdk://app";
    String[] data;

    private String providerName;
    private String userID;
    private String amountRecharge;

    @BindView(R.id.btnRecharge)
    Button btnRecharge;

    public void getData(String[] data){
        this.providerName = data[0];
        this.userID = data[1];
        this.amountRecharge = data[2];
        Log.d(TAG, "onCreate: get Data: providerName: " + providerName);
        Log.d(TAG, "onCreate: get Data: userID: " + userID);
        Log.d(TAG, "onCreate: get Data: amountRecharge: " + amountRecharge);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate Activity");
        Bundle bundle = getIntent().getExtras();
        data = bundle.getStringArray("data");
        getData(data);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    @Override
    public void onBackPressed() {
//        setResult(AppCompatActivity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @OnClick(R.id.btnRecharge)
    public void newRechargeZaloPay(){
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrderV2(amountRecharge);
            handleZaloPayResultV2(data);


        } catch (Exception e) {
            Log.e(TAG, "newRechargeZaloPay: Exception" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleZaloPayResultV2(JSONObject data) {
        try {
            String code = data.getString("return_code");
            String sub_code = data.getString("sub_return_code");
            String message = data.getString("return_message");
            String sub_message = data.getString("sub_return_message");
            Log.d(TAG, "handleZaloPayResultV2: return_code: " + code);
            Log.d(TAG, "handleZaloPayResultV2: sub_code: " + sub_code);
            Log.d(TAG, "handleZaloPayResultV2: message: " + message);
            Log.d(TAG, "handleZaloPayResultV2: sub_message: " + sub_message);

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                Log.d(TAG, "handleZaloPayResultV2: token: "+ token);
                String order_url = data.getString("order_url");
                Log.d(TAG, "handleZaloPayResultV2: order_url: " + order_url);

                MerchantService zaloPayInstance = ZaloPaySDK.getInstance();
//                zaloPayInstance.navigateToZaloPayOnStore(RechargeActivity.this);
                zaloPayInstance.payOrder(RechargeActivity.this,token,schema, new MyZaloPayListener());
                Log.e(TAG, "newRechargeZaloPay: passing payOrder" );
            }
            if (code.equals("2")) {
                Toast.makeText(RechargeActivity.this, message, Toast.LENGTH_SHORT).show();
                Toast.makeText(RechargeActivity.this, sub_message, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Log.e(TAG, "handleZaloPayResultV2: Error_Exception:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class MyZaloPayListener implements PayOrderListener {
        @Override
        public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
            //Handle Success
            Log.d(TAG, "onPaymentSucceeded: transactionId: " + transactionId);
            Log.d(TAG, "onPaymentSucceeded: transToken: " + transToken);
            Log.d(TAG, "onPaymentSucceeded: appTransID: " + appTransID);
        }

        @Override
        public void onPaymentCanceled(String zpTransToken, String appTransID) {
            //Handle User Canceled
            Log.d(TAG, "onPaymentCanceled: ");
        }
        @Override
        public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
            //Redirect to Zalo/ZaloPay Store when zaloPayError == ZaloPayError.PAYMENT_APP_NOT_FOUND
            //Handle Error
            Log.e(TAG, "onPaymentError: " + zaloPayError.toString());

            if (zaloPayError == ZaloPayError.PAYMENT_APP_NOT_FOUND ){


                // ZaloPaySDK.sharedInstance()?.navigateToZaloPayStore();   // navigator to ZaloPay App

                return;
            }
        }
    }

}
