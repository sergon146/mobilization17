package com.sergon146.mobilization17.history;

import android.content.Context;

import com.sergon146.mobilization17.BasePresenter;
import com.sergon146.mobilization17.BaseView;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

public interface HistoryContract {
    interface View extends BaseView<Presenter> {
        void hideSearchView();

        void showData(List<Translate> translateList);

        void hideData();

        void showEmpty();

        void hideEmpty();

        void clearSearch();

        void hideTrash();

        void showTrash();
    }

    interface Presenter extends BasePresenter {
        void loadData();

        void clearData();

        void setFavourite(Translate translate);

        void search(String searchText);

        void showDialog(Context context, android.view.View v);
    }


    interface TranslateItemListener {
        void onFavouriteClick(Translate translate);

        void onTextClick(Translate translate);
    }
}
