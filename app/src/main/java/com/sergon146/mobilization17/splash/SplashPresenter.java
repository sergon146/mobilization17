package com.sergon146.mobilization17.splash;

import android.util.Log;

import com.sergon146.mobilization17.data.source.TranslateRepository;
import com.sergon146.mobilization17.data.source.local.DbBackend;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.util.Const;

import java.util.List;
import java.util.Locale;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SplashPresenter implements SplashContract.Presenter {
    private CompositeSubscription mSubscriptions;
    private SplashContract.View mView;
    private TranslateRepository mRepository;

    public SplashPresenter(SplashContract.View view, TranslateRepository repository) {
        mView = view;
        mView.setPresenter(this);
        mRepository = repository;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        loadLanguagesIfNecessary();
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void loadLanguagesIfNecessary() {
        if (mRepository.isEmptyLangList(Locale.getDefault().getLanguage())) {
            Subscription subscription = mRepository.loadLangs(Locale.getDefault().getLanguage())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Language>>() {
                        @Override
                        public void onNext(List<Language> languages) {
                            mRepository.saveLanguages(Locale.getDefault().getLanguage(), languages);
                        }

                        @Override
                        public void onCompleted() {
                            Log.i(Const.LOG_TAG, "Langs load success!");
                            mView.finishSplash();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.showErrorLoadToast();
                            mView.finishSplash();
                        }
                    });

            mSubscriptions.add(subscription);
        } else {
            mView.finishSplash();
        }
    }

    public void setLanguage(List<Language> languages) {
        if (languages != null) {
            DbBackend backend = new DbBackend(null);
            backend.insertLanguages(Locale.getDefault().getLanguage(), languages);
        }

    }
}
