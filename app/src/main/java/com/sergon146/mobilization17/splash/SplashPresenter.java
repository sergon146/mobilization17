package com.sergon146.mobilization17.splash;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sergon146.mobilization17.data.TranslateRepository;
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

    //проверяем при загрузке наличие списка языков
    @Override
    public void subscribe() {
        loadLanguagesIfNecessary(Locale.getDefault().getLanguage());
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    }

    @Override
    public void loadLanguagesIfNecessary(String localeCode) {
        if (mRepository.isEmptyLangList(localeCode)) {
            Subscription subscription = mRepository.loadLangs(localeCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Language>>() {
                        @Override
                        public void onNext(List<Language> languages) {
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
}
