package com.dream.dreamtheather.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int MAX_ITEM_TAB = 3;

    ViewPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0 : return new SpotlightFragment();
            case 1 : return new NowShowingFragment();
            case 2 : return new UpcomingFragment();
        }
        return new SpotlightFragment();
    }

    @Override
    public int getItemCount() {
        return MAX_ITEM_TAB;
    }
}
