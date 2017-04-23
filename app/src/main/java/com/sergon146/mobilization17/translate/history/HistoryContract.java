package com.sergon146.mobilization17.translate.history;

import android.content.Context;

import com.sergon146.mobilization17.BasePresenter;
import com.sergon146.mobilization17.BaseView;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

public interface HistoryContract {
    interface View extends BaseView<Presenter> {
        /**
         * Hide search field
         */
        void hideSearchView();

        /**
         * Show list of translations
         *
         * @param translateList list of translation
         */
        void showData(List<Translate> translateList);

        /**
         * Hide all translations from screen
         */
        void hideData();

        /**
         * Show "Empty" message
         */
        void showEmpty();

        /**
         * Hide "Empty" message
         */
        void hideEmpty();

        /**
         * Clear search field
         */
        void clearSearch();

        /**
         * Hide "Delete" button
         */
        void hideTrash();

        /**
         * Show "Delete" button
         */
        void showTrash();
    }

    interface Presenter extends BasePresenter {
        /**
         * Load data from DB
         */
        void loadData();

        /**
         * Delete all data from DB
         */
        void clearData();

        /**
         * Set chosen translateion as favourite
         *
         * @param translate chosen translate
         */
        void setFavourite(Translate translate);

        /**
         * Search translate whice contains searchText in source or target text
         *
         * @param searchText search query param
         */
        void search(String searchText);

        /**
         * Show Alert Dialog
         *
         * @param context Context
         */
        void showDialog(Context context);

        /**
         * Delete chosen translate from DB
         *
         * @param translate chosen translate
         */
        void deleteTranslate(Translate translate);
    }


    interface TranslateItemListener {

        /**
         * Listener for item favourite icon click
         *
         * @param translate chosen translate
         */
        void onFavouriteClick(Translate translate);

        /**
         * Listener for item click
         *
         * @param translate chosen translate
         */
        void onItemClick(Translate translate);
    }
}
