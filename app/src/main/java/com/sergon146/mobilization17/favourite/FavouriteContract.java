package com.sergon146.mobilization17.favourite;

import com.sergon146.mobilization17.BasePresenter;
import com.sergon146.mobilization17.BaseView;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

public interface FavouriteContract {
    interface View extends BaseView<Presenter> {
        void showSearchView();

        void hideSearchView();

        void showData(List<Translate> translateList);

        void hideData();

        void showEmpty();

        void hideEmpty();
    }

    interface Presenter extends BasePresenter {
        void loadData();

        void cleanFavourite();

        void setFavourite(Translate translate);

        void searchInFavourite(String searchText);
    }
}
