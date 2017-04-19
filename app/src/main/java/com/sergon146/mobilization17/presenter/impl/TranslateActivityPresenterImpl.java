package com.sergon146.mobilization17.presenter.impl;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.activity.TranslateActivity;
import com.sergon146.mobilization17.fragment.FavouriteFragment;
import com.sergon146.mobilization17.fragment.HistoryFragment;
import com.sergon146.mobilization17.fragment.TranslateFragment;
import com.sergon146.mobilization17.presenter.TranslateActivityPresenter;

import java.util.ArrayList;
import java.util.List;

public class TranslateActivityPresenterImpl implements TranslateActivityPresenter {
    private final TranslateActivity activity;
    private final List<Fragment> fragmentList;
    private int currentItem = 0;

    public TranslateActivityPresenterImpl(TranslateActivity activity) {
        this.activity = activity;
        fragmentList = new ArrayList<>();
        fragmentList.add(TranslateFragment.newInstance(0));
        fragmentList.add(HistoryFragment.newInstance(1));
        fragmentList.add(FavouriteFragment.newInstance(2));

        activity.setCurrentFragment(fragmentList.get(0));
    }

    public void setCurrentFragmentByItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_translate:
                activity.setCurrentFragment(fragmentList.get(0));
                currentItem = 0;
                break;
            case R.id.navigation_history:
                activity.setCurrentFragment(fragmentList.get(1));
                currentItem = 1;
                break;
            case R.id.navigation_favourite:
                activity.setCurrentFragment(fragmentList.get(2));
                currentItem = 2;
                break;
        }
    }

    public int getCurrentItem() {
        return currentItem;
    }
}
