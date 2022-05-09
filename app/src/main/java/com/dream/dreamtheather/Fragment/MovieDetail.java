package com.dream.dreamtheather.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetail extends Fragment {
    private static final String TAG = "MovieDetail";
    /** Phim nguồn cần xem chi tiết */
    Movie mMovie;

    @BindView(R.id.back_image_view)
    ImageView mBackImageView;
    @BindView(R.id.avatar) ImageView mAvatarImageView;
    @BindView(R.id.title)
    TextView mTitleTextView;
    @BindView(R.id.opening_day) TextView mDescriptionTextView;

    @BindView(R.id.content_text_view) TextView mContentTextView;
    @BindView(R.id.category) TextView mCategoryTextView;
    @BindView(R.id.release) TextView mReleaseTextView;
    @BindView(R.id.director) TextView mDirectorTextView;
    @BindView(R.id.cast) TextView mCastTextView;


    @BindView(R.id.back_button)
    View mBackButton;
    @BindView(R.id.book_now_button)
    FloatingActionButton mBookNowButton;

    @OnClick(R.id.play_panel)
    void doSomething() {
//        requireActivity().presentFragment(WebViewFragment.newInstance(mMovie.getTrailerYoutube()));

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(mMovie.getTrailerYoutube()+"?autoplay=1"));
        startActivity(i);
    }

    @OnClick(R.id.book_now_button)
    void goToBooking() {
        ((MainActivity)getActivity()).loadFragment(BookingFragment.newInstance(mMovie));
    }

    @OnClick(R.id.back_button)
    void dismiss() {
        //getMainActivity().dismiss();
    }

    /**
     * Tạo Fragment Movie Detail để xem chi tiết một phim
     * @param movie Phim cần xem chi tiết
     * @return  Đối tượng MovieDetail
     */
    public static MovieDetail newInstance(Movie movie) {
        MovieDetail md = new MovieDetail();

        md.mMovie = movie;
        return md;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        bind(mMovie);
    }
    private void bind(Movie movie) {
        if(movie!=null) {

            mTitleTextView.setText(movie.getTitle());
            mCastTextView.setText(movie.getCast());
            mDescriptionTextView.setText(movie.getOpeningDay());
            mCategoryTextView.setText(movie.getGenre());
            mDirectorTextView.setText(movie.getDirector());
            mReleaseTextView.setText(movie.getOpeningDay());

            mContentTextView.setText(movie.getDescription());

            RequestOptions requestOptions = new RequestOptions().override(mAvatarImageView.getWidth());
            Glide.with(getContext())
                    .load(movie.getImageUrl())
                    .apply(requestOptions)
                    .into(mAvatarImageView);

            RequestOptions requestOptions2 = new RequestOptions().override(mAvatarImageView.getWidth());
            Glide.with(getContext())
                    .load(movie.getImageUrl())
                    .apply(requestOptions2)
                    .into(mBackImageView);
        }
    }

}
