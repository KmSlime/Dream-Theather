package com.dream.dreamtheather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dream.dreamtheather.Fragment.BookingFragment;
import com.dream.dreamtheather.Fragment.MovieDetail;
import com.dream.dreamtheather.Fragment.SpotlightFilmFragment;
import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Movie;
import com.dream.dreamtheather.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpotlightViewPagerAdapter extends LoopingPagerAdapter {

    private static final String TAG = "SpotlightViewPagerAdapter";
    Context mContext;
    List<Movie> mMovieList ;

    public SpotlightViewPagerAdapter(@NonNull List itemList, boolean isInfinite) {
        super(itemList, isInfinite);
        this.mMovieList = itemList;
    }

    @Override
    public int getCount() {
        return mMovieList.size();
    }

    public void setData(List<Movie> data) {
        if (data !=null) {
            mMovieList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void addData(List<Movie> data) {
        if(data!=null) {
            int posBefore = mMovieList.size();
            mMovieList.addAll(data);
        }
    }

    @NonNull
    @Override
    public void bindView(@NonNull View convertView, int position, int viewType) {
        convertView.setTag(ItemHolder.class);
//        itemHolder.bind(mMovieList.get(i));
    }

    @NonNull
    @Override
    protected View inflateView(int viewType, @NonNull ViewGroup container, int listPosition) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        return inflater.inflate(R.layout.item_movie_spotlight, container, false);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image)
        ImageView mImage;

        @BindView(R.id.title)
        TextView mTitle;

        @BindView(R.id.note_text)
        TextView mNote;
        @BindView(R.id.rate)
        TextView mRate;

        public ItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
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
            Log.d(TAG, "bind: " + title);
            mTitle.setText(upperCaseAllFirst(title));
            mNote.setText(movie.getOpeningDay());
            mRate.setText(String.format("%s", movie.getRate()));

            RequestOptions requestOptions = new RequestOptions();
            Glide.with(mContext)
                    .load(movie.getImageUrl())
                    .apply(requestOptions)
                    .into(mImage);
        }

        @Override
        public void onClick(View v) {
            if (mContext instanceof MainActivity) {
                ((MainActivity) mContext).loadFragment(MovieDetail.newInstance(mMovieList.get(getBindingAdapterPosition())));
            }
        }

        @OnClick(R.id.book)
        void goToBook() {
            if (mContext instanceof MainActivity) {
                ((MainActivity) mContext).loadFragment(BookingFragment.newInstance(mMovieList.get(getBindingAdapterPosition())));
            }
        }
    }
}
