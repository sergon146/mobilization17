package com.sergon146.mobilization17.favourite;

import com.sergon146.mobilization17.data.source.TranslateRepository;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FavPresenter implements FavouriteContract.Presenter {
    private TranslateRepository repository;
    private FavouriteContract.View mView;

    public FavPresenter(TranslateRepository repository, FavouriteContract.View mView) {
        this.repository = repository;
        this.mView = mView;

        this.mView.setPresenter(this);
    }

    public void subscribe() {
        loadData();
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void loadData() {
        repository.loadFavourites()
                .subscribeOn(Schedulers.io())
                .toList()
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
                        if(translates.isEmpty())
                            mView.hideData();
                        else
                            mView.showData(translates);
                    }
                });
    }

    @Override
    public void cleanFavourite() {

    }

    @Override
    public void setFavourite(Translate translate) {

    }

    @Override
    public void searchInFavourite(String searchText) {

    }
}
