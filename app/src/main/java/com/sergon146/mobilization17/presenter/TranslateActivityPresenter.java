package com.sergon146.mobilization17.presenter;

import android.view.MenuItem;

public interface TranslateActivityPresenter {
    void setCurrentFragmentByItem(MenuItem item);

    int getCurrentItem();
}
