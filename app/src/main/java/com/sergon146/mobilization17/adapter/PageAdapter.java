package com.sergon146.mobilization17.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sergon146.mobilization17.fragment.FavouriteFragment;
import com.sergon146.mobilization17.fragment.HistoryFragment;
import com.sergon146.mobilization17.fragment.TranslateFragment;

import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments = new ArrayList<>();

    public PageAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(0, TranslateFragment.newInstance(0));
        fragments.add(1, HistoryFragment.newInstance(1));
        fragments.add(2, FavouriteFragment.newInstance(2));
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public List<Fragment> getFragments() {
        return fragments;
    }
}
