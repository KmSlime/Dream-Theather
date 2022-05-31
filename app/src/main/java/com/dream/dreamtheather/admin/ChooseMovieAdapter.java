package com.dream.dreamtheather.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dream.dreamtheather.Model.Movie;
import com.dream.dreamtheather.R;
import com.dream.dreamtheather.Model.Movie;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseMovieAdapter extends RecyclerView.Adapter<ChooseMovieAdapter.ItemHolder> {
    private static final String TAG ="ChooseMovieAdapter";

    private List<Movie> mMovieData = new ArrayList<>();
    private List<Movie> mSavedSelectedMovieData = new ArrayList<>();
    private List<Boolean> mSelectedData = new ArrayList<>();
    private Context mContext;
    int count = 0;
    public interface CountingCallBack {
        void onCountChanged(int newValue);
    }
    private CountingCallBack mCountingCallBack;
    public void setCountingCallBack(CountingCallBack callBack) {
        mCountingCallBack = callBack;

        if(callBack!=null)
        callBack.onCountChanged(count);
    }

    ChooseMovieAdapter(Context context) {
        this.mContext = context;
    }
    private void setCount(int value) {
        count = value;
        if(mCountingCallBack!=null) mCountingCallBack.onCountChanged(value);
    }

    public void setMovieData(List<Movie> data) {
        mMovieData.clear();
        mSelectedData.clear();
        setCount(0);
        if (data !=null) {
            mMovieData.addAll(data);

        }
        getSelectedIndex();
        notifyDataSetChanged();
    }
    public List<Movie> getSavedSelectedData() {
        return mSavedSelectedMovieData;
    }
    private void getSelectedIndex() {
        mSelectedData.clear();
        setCount(0);

        for (Movie ignored : mMovieData) mSelectedData.add(false);

        for (int i = 0; i < mMovieData.size(); i++) {

            for (int j = 0; j < mSavedSelectedMovieData.size(); j++) {
                if (mMovieData.get(i).getId() == mSavedSelectedMovieData.get(j).getId()) {

                    Log.d(TAG, "setSelectedData: detect selected i = " + i);
                    setCount(count + 1);
                    mSelectedData.set(i, true);
                    break;
                }
            }

        }
    }

    public void setSelectedData(List<Movie> data) {
        mSavedSelectedMovieData.clear();
        if(data!=null)
        mSavedSelectedMovieData.addAll(data);

       getSelectedIndex();
       notifyDataSetChanged();
    }
    public ArrayList<Movie> getSelectedData() {
        ArrayList<Movie> data = new ArrayList<>();
        for (int i = 0; i < mMovieData.size(); i++) {
            if(mSelectedData.get(i)) data.add(mMovieData.get(i));
        }
        return data;
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_admin_choose_movie, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(mMovieData.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovieData.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.panel)
        View mPanel;

        @BindView(R.id.checkbox)
        AppCompatCheckBox mCheckbox;

        @BindView(R.id.img)
        RoundedImageView mImg;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_id)
        TextView mID;

        @BindView(R.id.txt_release)
        TextView mReleaseDay;


        ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        @OnClick(R.id.panel)
        void clickPanel() {
            boolean b = mSelectedData.get(getAdapterPosition());
            mSelectedData.set(getAdapterPosition(),!mSelectedData.get(getAdapterPosition()));
            b = !b;
            if(b) setCount(count+1); else setCount(count-1);
            notifyItemChanged(getAdapterPosition());
        }

        @SuppressLint("DefaultLocale")
        public void bind(Movie movie) {

            mName.setText(movie.getTitle());
            mID.setText(String.format("%d", movie.getId()));
            mReleaseDay.setText(movie.getOpeningDay());

            if(mSelectedData.size()>getAdapterPosition() && mSelectedData.get(getAdapterPosition())) {
                Log.d(TAG, "bind: select position = "+getAdapterPosition());
                mPanel.setBackgroundResource(R.drawable.black_rounded_big_selected);
                if(!mCheckbox.isChecked())
                mCheckbox.setChecked(true);
            }
            else {
                mPanel.setBackgroundResource(R.drawable.black_rounded_big);
                if(mCheckbox.isChecked())
                mCheckbox.setChecked(false);
            }

            RequestOptions requestOptions = new RequestOptions().override(mImg.getWidth());
            Glide.with(mContext)
                    .load(movie.getImageUrl())
                    .apply(requestOptions)
                    .into(mImg);

        }
    }
}
