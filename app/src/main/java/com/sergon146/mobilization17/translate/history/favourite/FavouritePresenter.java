package com.sergon146.mobilization17.translate.history.favourite;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.data.TranslateRepository;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.translate.history.HistoryContract;
import com.sergon146.mobilization17.util.Const;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FavouritePresenter implements HistoryContract.Presenter {
    private TranslateRepository mRepository;
    private HistoryContract.View mView;
    private CompositeSubscription mSubscription;

    public FavouritePresenter(TranslateRepository repository, HistoryContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        loadData();
    }

    @Override
    public void unsubscribe() {
        mSubscription.clear();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        loadLanguagesIfNecessary(Locale.getDefault().getLanguage());
    }

    @Override
    public void loadLanguagesIfNecessary(String localeCode) {
        if (mRepository.isEmptyLangList(localeCode)) {
            mRepository.loadLangs(localeCode);
        }
    }

    @Override
    public void loadData() {
        Subscription subscription = mRepository.loadFavourites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Translate>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Translate> translates) {
                        if (translates.isEmpty()) {
                            mView.hideData();
                            mView.hideSearchView();
                            mView.showEmpty();
                            mView.hideTrash();
                        } else {
                            mView.showData(translates);
                            mView.hideEmpty();
                            mView.showTrash();
                        }
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void clearData() {
        mRepository.clearFavourites();
    }

    @Override
    public void setFavourite(Translate translate) {
        mRepository.setFavourites(translate);
    }

    @Override
    public void search(String searchText) {
        mRepository.searchInFavourite(searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Translate>>() {
                    @Override
                    public void onCompleted() {
                        Log.i(Const.LOG_TAG, "Search complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(Const.LOG_TAG, "Error while searching");
                        mView.showData(Collections.emptyList());
                    }

                    @Override
                    public void onNext(List<Translate> translates) {
                        if (translates.isEmpty()) {
                            mView.hideData();
                            mView.showEmpty();
                        } else {
                            mView.hideEmpty();
                            mView.showData(translates);
                        }
                    }
                });
    }

    @Override
    public void showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.favourite_dialog_title))
                .setNegativeButton(context.getString(R.string.no),
                        (dialog, idV) -> dialog.cancel())
                .setPositiveButton(context.getString(R.string.yes),
                        (dialog, idV) -> {
                            dialog.cancel();
                            mView.hideTrash();
                            mView.hideData();
                            mView.hideSearchView();
                            mView.showEmpty();
                            clearData();
                        })
                .create()
                .show();
    }

    @Override
    public void deleteTranslate(Translate translate) {
        mRepository.deleteTranslate(translate);
    }
}
