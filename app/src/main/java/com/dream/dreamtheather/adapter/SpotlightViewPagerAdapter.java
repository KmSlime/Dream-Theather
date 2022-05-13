package com.dream.dreamtheather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dream.dreamtheather.Fragment.SpotlightFilmFragment;
import com.dream.dreamtheather.Model.Movie;

import java.util.List;

public class SpotlightViewPagerAdapter extends FragmentStateAdapter {

    Context mContext;
    List<Movie> mMovieList ;

    public SpotlightViewPagerAdapter(@NonNull FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    public SpotlightViewPagerAdapter(Fragment fragment, List<Movie> movieList){
        super(fragment);
        mContext = fragment.getContext();
        mMovieList = movieList;
    }

    public SpotlightViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        mContext = fragment.getContext();
    }

    public SpotlightViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return SpotlightFilmFragment.newInstance(mMovieList.get(position));
    }

    @Override
    public int getItemCount() {
        if(mMovieList != null)
            return mMovieList.size();
        return 1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Movie> data) {
        if (data !=null) {
            mMovieList.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(List<Movie> data) {
        if(data!=null) {
            int posBefore = mMovieList.size();
            mMovieList.addAll(data);
            notifyItemRangeInserted(posBefore,data.size());
        }
    }
}
