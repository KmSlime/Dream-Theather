package com.dream.dreamtheather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dream.dreamtheather.helper.CustomPlayerUiController;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController;

public class Test extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_youtube);
        initViews();
        initYoutubeView();
    }

    private void initViews() {
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
    }

    private void initYoutubeView(){

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "daHCu_jU5mQ";
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
        defaultPlayerUI();
    }

    private void defaultPlayerUI(){
        youTubePlayerView.setEnableAutomaticInitialization(false);
        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // using pre-made custom ui
                DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(youTubePlayerView, youTubePlayer);
                youTubePlayerView.setCustomPlayerUi(defaultPlayerUiController.getRootView());
                defaultPlayerUiController.showFullscreenButton(true);
                defaultPlayerUiController.showCurrentTime(true);
                defaultPlayerUiController.showDuration(true);
                defaultPlayerUiController.setFullScreenButtonClickListener(view -> {
                    boolean isFullScreen = youTubePlayerView.isFullScreen();
                    Log.v("isFullScreen Value = ",String.valueOf(isFullScreen));
                    if(!isFullScreen)
                    {
                        youTubePlayerView.enterFullScreen();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                    else
                    {
                        youTubePlayerView.exitFullScreen();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }

                });
            }
        };

// disable iframe ui
        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
        youTubePlayerView.initialize(listener, options);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    private void customPlayerUI() {
        youTubePlayerView.setEnableAutomaticInitialization(false);
        View customPlayerUi = youTubePlayerView.inflateCustomPlayerUi(R.layout.custom_video_player_ui);

        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {

            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                CustomPlayerUiController customPlayerUiController = new CustomPlayerUiController(Test.this, customPlayerUi, youTubePlayer, youTubePlayerView);
                youTubePlayer.addListener(customPlayerUiController);
                youTubePlayerView.addFullScreenListener(customPlayerUiController);

                YouTubePlayerUtils.loadOrCueVideo(
                        youTubePlayer, getLifecycle(),
                        "d44UTUSTYKU",0f
                );
            }
        };

        // disable iframe ui
        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
        youTubePlayerView.initialize(listener, options);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }
}