package com.dream.dreamtheather.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dream.dreamtheather.Fragment.BookingFragment;
import com.dream.dreamtheather.Fragment.MovieDetail;
import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Movie;
import com.dream.dreamtheather.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpotlightAdapter extends RecyclerView.Adapter<SpotlightAdapter.ItemHolder> {
    private static final String TAG ="HomeChildAdapter";

    private List<Movie> mData = new ArrayList<>();
    Context mContext;

    public SpotlightAdapter(Context context) {
        mContext = context;
    }
    public void setData(List<Movie> data) {
        mData.clear();
        if (data !=null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(List<Movie> data) {
        if(data!=null) {
            int posBefore = mData.size();
            mData.addAll(data);
            notifyItemRangeInserted(posBefore,data.size());
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new ItemHolder(inflater.inflate(R.layout.item_movie_spotlight,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        itemHolder.bind(mData.get(i));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image)
        ImageView mImage;

        @BindView(R.id.title)
        TextView mTitle;

        @BindView(R.id.note_text) TextView mNote;
        @BindView(R.id.rate) TextView mRate;
        public ItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this,itemView);
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
            Log.d(TAG, "bind: " +title);
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
            if(mContext instanceof MainActivity) {
                ((MainActivity) mContext).loadFragment(MovieDetail.newInstance(mData.get(getBindingAdapterPosition())));
            }
        }

        @OnClick(R.id.book)
        void goToBook() {
            if(mContext instanceof MainActivity) {
                ((MainActivity) mContext).loadFragment(BookingFragment.newInstance(mData.get(getBindingAdapterPosition())));
            }
        }
    }
}
