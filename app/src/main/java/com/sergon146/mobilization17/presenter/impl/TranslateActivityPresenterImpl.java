package com.sergon146.mobilization17.presenter.impl;

import android.view.MenuItem;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.activity.TranslateActivity;
import com.sergon146.mobilization17.presenter.TranslateActivityPresenter;

public class TranslateActivityPresenterImpl implements TranslateActivityPresenter {
    private TranslateActivity activity;

    public TranslateActivityPresenterImpl(TranslateActivity activity) {
        this.activity = activity;
    }


    public void setCurrentFragmentByItem(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_translate:
                activity.setCurrentItem(0);
                break;
            case R.id.navigation_history:
                activity.setCurrentItem(1);
                break;
            case R.id.navigation_favourite:
                activity.setCurrentItem(2);
                break;
        }
    }
}
