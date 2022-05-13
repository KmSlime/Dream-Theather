package com.dream.dreamtheather.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Movie;
import com.dream.dreamtheather.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpotlightFilmFragment extends Fragment {
    private static final String TAG = "SpotlightFilm";
    public Movie movie;

    @BindView(R.id.image)
    ImageView mImage;

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.note_text)
    TextView mNote;
    @BindView(R.id.rate)
    TextView mRate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_movie_spotlight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        bind(movie);
    }

    public static SpotlightFilmFragment newInstance(Movie movie) {
        SpotlightFilmFragment fragment = new SpotlightFilmFragment();
        fragment.movie = movie;
        return fragment;
    }

    public String upperCaseAllFirst(String value) {

        char[] array = value.toCharArray();
        // Uppercase first letter.
        array[0] = Character.toUpperCase(array[0]);

        // Uppercase all letters that follow a whitespace character.
        for (int i = 1; i < array.length; i++) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i]);
            }
        }

        // Result.
        return new String(array);
    }

    private void bind(Movie movie) {
        String title = movie.getTitle().toLowerCase();
        title = upperCaseAllFirst(title);
//        Log.d(TAG, "bind: " + title);
        mTitle.setText(upperCaseAllFirst(title));
        mNote.setText(movie.getOpeningDay());
        mRate.setText(String.format("%s", movie.getRate()));

        RequestOptions requestOptions = new RequestOptions();
        Glide.with(getContext())
                .load(movie.getImageUrl())
                .apply(requestOptions)
                .into(mImage);
    }

    @OnClick(R.id.panel)
    public void onClick(View v) {
        if (getContext() instanceof MainActivity) {
            ((MainActivity) getContext())
                    .loadFragment(MovieDetail.newInstance(movie));
        }
    }

    @OnClick(R.id.book)
    void goToBook() {
        if (getContext() instanceof MainActivity) {
            ((MainActivity) getContext())
                    .loadFragment(BookingFragment.newInstance(movie));
        }
    }

}
