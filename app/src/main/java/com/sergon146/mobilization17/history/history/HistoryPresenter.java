package com.sergon146.mobilization17.history.history;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.data.source.TranslateRepository;
import com.sergon146.mobilization17.history.HistoryContract;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.util.Const;

import java.util.Collections;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class HistoryPresenter implements HistoryContract.Presenter {
    private TranslateRepository mRepository;
    private HistoryContract.View mView;
    private CompositeSubscription mSubscription;

    public HistoryPresenter(TranslateRepository repository, HistoryContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
        mSubscription = new CompositeSubscription();
    }

    public void subscribe() {
        loadData();
    }

    @Override
    public void unsubscribe() {
        mSubscription.clear();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //do nothing
    }

    @Override
    public void loadLanguagesIfNecessary(String localeCode) {
        if (mRepository.isEmptyLangList(localeCode)) {
            mRepository.loadLangs(localeCode);
        }
    }

    @Override
    public void loadData() {
        Subscription subscription = mRepository.loadHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        t -> {
                            if (t.isEmpty()) {
                                mView.hideData();
                                mView.hideSearchView();
                                mView.showEmpty();
                                mView.hideTrash();
                            } else {
                                mView.showData(t);
                                mView.hideEmpty();
                                mView.showTrash();
                            }
                        },
                        e -> Log.w(Const.LOG_TAG, e.toString()));
        mSubscription.add(subscription);
    }

    @Override
    public void clearData() {
        mRepository.clearHistory();
    }

    @Override
    public void setFavourite(Translate translate) {
        mRepository.setFavourites(translate);
    }

    @Override
    public void search(String searchText) {
        mRepository.searchInHistory(searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        translates -> {
                            if (translates.isEmpty()) {
                                mView.hideData();
                                mView.showEmpty();
                            } else {
                                mView.hideEmpty();
                                mView.showData(translates);
                            }
                        },
                        e -> {
                            Log.w(Const.LOG_TAG, "Error while searching");
                            mView.showData(Collections.emptyList());
                        },
                        () -> Log.i(Const.LOG_TAG, "Search complete")
                );

    }

    @Override
    public void showDialog(Context context, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.history_dialog_title))
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
}

