package com.dream.dreamtheather.Fragment;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dream.dreamtheather.Model.Movie;
import com.dream.dreamtheather.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YoutubeViewFragment extends Fragment {
    public static final String TAG = "YoutubeViewFrag";
    String mURL;
    String mID;

    @BindView(R.id.youtube_player_view)
    YouTubePlayerView mYouTubePlayerView;

    public static Fragment newInstance(String url) {
        YoutubeViewFragment fragment = new YoutubeViewFragment();
        fragment.mURL = url;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initViews();
    }

    public void initViews() {
        getLifecycle().addObserver(mYouTubePlayerView);
        mYouTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(getYoutubeVideoID(mURL), 0);
            }
        });
        defaultPlayerUI();
    }

    public String getYoutubeVideoID(String mURL) {
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(mURL);
        if (matcher.matches()) {
            mID = matcher.group(1);
        }
        return mID;
    }

    public void defaultPlayerUI() {
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
                    Log.v("isFullScreen Value = ", String.valueOf(isFullScreen));
                    if (!isFullScreen) {
                        mYouTubePlayerView.enterFullScreen();
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        mYouTubePlayerView.exitFullScreen();
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
//            Toast.makeText(this.getActivity(), "landscape", Toast.LENGTH_SHORT).show();
            Log.v(TAG,"screen is landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
             Log.v(TAG,"screen is portrait");
//            Toast.makeText(this.getActivity(), "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mYouTubePlayerView.release();
    }
}
