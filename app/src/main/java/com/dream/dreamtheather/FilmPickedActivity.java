package com.dream.dreamtheather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dream.dreamtheather.adapter.DetailFilmAdapter;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController;

public class FilmPickedActivity extends AppCompatActivity implements View.OnClickListener {

    public String YOUTUBE_FILM_ID = "daHCu_jU5mQ";
    private YouTubePlayerView mYouTubePlayerView;
    int FILM_ID;

    ImageView btnBack;
    Button btnDatVe, btnXemThemUuDai;

    DetailFilmAdapter detailFilmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_picked);
        initViews();
        initYoutubeView();
        handleFilmAdapter();
    }

    private void handleFilmAdapter() {
        detailFilmAdapter = new DetailFilmAdapter(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_dat_ve:
                gotoOrder(FILM_ID);
                break;
            case R.id.btn_show_all_promo:
                break;
        }
    }

    public void initViews() {
        mYouTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(mYouTubePlayerView);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this::onClick);
        btnDatVe = findViewById(R.id.btn_dat_ve);
        btnXemThemUuDai = findViewById(R.id.btn_show_all_promo);
    }

    private void initYoutubeView(){
        mYouTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(YOUTUBE_FILM_ID, 0);
            }
        });
        defaultPlayerUI();
    }

    private void defaultPlayerUI(){
        mYouTubePlayerView.setEnableAutomaticInitialization(false);
        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // using pre-made custom ui
                DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(mYouTubePlayerView, youTubePlayer);
                mYouTubePlayerView.setCustomPlayerUi(defaultPlayerUiController.getRootView());
                defaultPlayerUiController.showFullscreenButton(true);
                defaultPlayerUiController.showCurrentTime(true);
                defaultPlayerUiController.showDuration(true);
                defaultPlayerUiController.setFullScreenButtonClickListener(view -> {
                    boolean isFullScreen = mYouTubePlayerView.isFullScreen();
                    Log.v("isFullScreen Value = ",String.valueOf(isFullScreen));
                    if(!isFullScreen)
                    {
                        mYouTubePlayerView.enterFullScreen();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                    else
                    {
                        mYouTubePlayerView.exitFullScreen();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }

                });
            }
        };

        // disable iframe ui
        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
        mYouTubePlayerView.initialize(listener, options);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoOrder(int film_id) {
        Intent intent = new Intent(this, Login.class);
        intent.putExtra("film_id_to_order",film_id);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
