package com.dream.dreamtheather.Fragment;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_PICTURES;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Ticket;
import com.dream.dreamtheather.Model.UserInfo;
import com.dream.dreamtheather.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PurchaseScreen extends Fragment {

    private static final String TAG = "PurchaseScreen";
    private static final String[] permission = new String[]{
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };
    private static final String[] permissionA11 = new String[]{MANAGE_EXTERNAL_STORAGE};
    private static final int SDK_INT = Build.VERSION.SDK_INT;
    private static final int PERMISSION_REQUEST_CODE = 2296;
    Context context;
    Activity activity;

    @BindView(R.id.user)
    TextView mUser;
    @BindView(R.id.movie)
    TextView mMovie;
    @BindView(R.id.cinema)
    TextView mCinema;
    @BindView(R.id.room)
    TextView mRoom;
    @BindView(R.id.seat)
    TextView mSeat;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.time)
    TextView mTime;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.btnShowQR)
    ImageButton btnShowQR;
    @BindView(R.id.btnSaveQR)
    Button btnSaveQR;

    Ticket mTicket;
    UserInfo user;

    Boolean canSaveQR = false;

    public static PurchaseScreen newInstance(Ticket t, UserInfo user) {
        PurchaseScreen p = new PurchaseScreen();
        p.mTicket = t;
        p.user = user;
        return p;
    }

    ActivityResultLauncher<String[]> activityResultLauncher;

    public PurchaseScreen() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Log.e("activityResultLauncher", "" + result.toString());
                    Boolean areAllGranted = true;
                    for (Boolean b : result.values()) {
                        areAllGranted = areAllGranted && b;
                    }
                    if (areAllGranted)
                        canSaveQR = true;
                    else
                        requestReadWriteExternalCard();
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ticket_print, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setContent();
        qrImage.setVisibility(View.GONE);
        btnSaveQR.setVisibility(View.GONE);
        this.activityResultLauncher.launch(permission);
    }

    void setContent() {
        String fullName = user.getFullName();
        mUser.setText(fullName);
        mMovie.setText(mTicket.getMovieName());
        mCinema.setText(mTicket.getCinemaName());
        mRoom.setText(mTicket.getRoom() + "");
        mSeat.setText(mTicket.getSeat());
        mDate.setText(mTicket.getDate());
        mTime.setText(mTicket.getTime());
        mPrice.setText(mTicket.getPrice() + "");
    }

    void getContent() {
        inputValue = "DreamCinema-Ticket-" + mTicket.getID() + "\n"
                + "Tên khách: " + mUser.getText().toString() + "\n"
                + "Phim: " + mMovie.getText().toString() + "\n"
                + "Rạp: " + mCinema.getText().toString() + "\n"
                + "Giờ chiếu: " + mTime.getText().toString() + "\n"
                + "Phòng: " + mRoom.getText().toString() + "\n"
                + "Ghế: " + mSeat.getText().toString()
        ;
    }

    @OnClick(R.id.back_button)
    void back() {
        ((MainActivity) getActivity()).restartHomeScreen();
    }

    String inputValue;
    final String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/";
    String imageNameScan;
    String filePathScan;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    @BindView(R.id.qrImg)
    ImageView qrImage;

    @OnClick(R.id.btnShowQR)
    public void showQR() {
        getContent();
        if (canSaveQR) {
            Toast.makeText(getActivity(), "Đã tạo mã QR cho vé của quý khách", Toast.LENGTH_SHORT).show();
            qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, getDimension());
            bitmap = qrgEncoder.getBitmap();
            qrImage.setVisibility(View.VISIBLE);
            qrImage.setImageBitmap(bitmap);
            btnSaveQR.setVisibility(View.VISIBLE);
        } else {
            requestReadWriteExternalCard();
        }
    }

    int getDimension() {
        WindowManager manager = (WindowManager) getActivity().getWindow().getWindowManager();
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        return smallerDimension;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    @OnClick(R.id.btnSaveQR)
    void saveQR() {
        String saveImgName = "DreamCinemaTicket-" + mTicket.getID();
        Log.v(TAG, "saveName: " + saveImgName);
        if (canSaveQR) {
            try {
                filePathScan = savePath + saveImgName + ".jpg";
                Log.v(TAG, "savePath: " + savePath);
                Log.v(TAG, "filePath: " + filePathScan);

                boolean save = new QRGSaver().save(savePath, saveImgName, bitmap, QRGContents.ImageType.IMAGE_JPEG);
                String result = save ? "Đã lưu vào Bộ sưu tập" : "Có lỗi khi lưu QR";
                //after save -> make it can be able see in gallery
                startScan();
                Log.v(TAG, result);
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Chưa cấp quyền truy cập bộ nhớ", Toast.LENGTH_LONG).show();
            requestReadWriteExternalCard();
        }
    }


    private void requestReadWriteExternalCard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", activity.getApplicationContext().getPackageName())));
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            }
        } else {
            //below android 11
            boolean isGrantedRWStorage = checkPermission();
            if (!isGrantedRWStorage)
                ActivityCompat.requestPermissions(activity, permission, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    canSaveQR = true;
                } else {
                    Toast.makeText(context, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        canSaveQR = true;
                    } else {
                        Toast.makeText(context, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                        requestReadWriteExternalCard();
                    }
                }
                break;
        }
    }

    MediaScannerConnection mediaScannerConnection;
    MediaScannerConnection.MediaScannerConnectionClient mediaScannerConnectionClient = new MediaScannerConnection.MediaScannerConnectionClient() {

        @Override
        public void onMediaScannerConnected() {
            try {
                Log.w(TAG, "onMediaScannerConnected: filePathScan: "+filePathScan);
                mediaScannerConnection.scanFile(filePathScan, "image/jpg");
            } catch (java.lang.IllegalStateException e) {
                Log.e(TAG, "onMediaScannerConnected: ",e);
                e.printStackTrace();
            }
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            if (path.equals(filePathScan))
                mediaScannerConnection.disconnect();
        }


    };

    private void startScan() {
        if (mediaScannerConnection != null) mediaScannerConnection.disconnect();
        mediaScannerConnection = new MediaScannerConnection(context, mediaScannerConnectionClient);
        mediaScannerConnection.connect();
    }

}
