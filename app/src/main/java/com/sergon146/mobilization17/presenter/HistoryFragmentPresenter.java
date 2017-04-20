package com.sergon146.mobilization17.presenter;

import android.view.View;

import com.sergon146.mobilization17.pojo.Translate;

public interface HistoryFragmentPresenter {
    void initRecycler(View layout);

    void getNewData();

    void updateTranslate(Translate translate);

    void deleteHistory();

    void showDialog(View v);
}
