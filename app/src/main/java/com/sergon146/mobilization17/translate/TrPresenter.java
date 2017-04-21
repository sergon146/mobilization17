package com.sergon146.mobilization17.translate;

import android.util.Log;

import com.sergon146.mobilization17.data.source.TranslateRepository;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.util.Util;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class TrPresenter implements TranslateContract.Presenter {
    private TranslateRepository mRepository;
    private TranslateContract.View mView;
    private CompositeSubscription mSubscription;

    private Translate translate;

    public TrPresenter(TranslateRepository repository, TranslateContract.View view) {
        translate = new Translate();
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mSubscription.clear();
    }

    @Override
    public void loadTranslate(String text) {
        mView.showProgress();
        translate.setSourceText(Util.trimAll(text));
        translate.setTargetText("");
        translate.setWordJson("");
        translate.setFavourite(false);
        translate.setWordMapper(null);

        Subscription subscription = mRepository.loadTranslateSentence(translate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Subscriber<Translate>() {
                    @Override
                    public void onNext(Translate tr) {
                        mView.setTargetText(tr.getTargetText());
                        if (!tr.getTargetText().isEmpty()) {
                            if (Util.isWord(text) && tr.getWordJson().isEmpty()) {
                                loadWord(tr);
                            } else {
                                translate.setWordJson("");
                                mRepository.saveTranslate(tr);
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideProgress();
                        mView.showButtons();
                        mView.changeFavourite(translate.isFavourite());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.showButtons();
                        mView.toastError();
                        Log.w("Translate", "Error while translated sentence: " + text + " " + e.toString());
                    }
                });

        mSubscription.add(subscription);
    }

    private void loadWord(Translate translate) {
        Subscription subscription = mRepository.loadTranslateWord(translate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Subscriber<Translate>() {
                    @Override
                    public void onNext(Translate tr) {
                        mView.setTargetText(tr.getTargetText() + "" + tr.getWordJson());
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideProgress();
                        mView.showButtons();
                        mView.changeFavourite(translate.isFavourite());
                        mRepository.saveTranslate(translate);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.showButtons();
                        Log.w("Dictionary", "Error while translated sentence: " + translate.getSourceText() + " - " + e);
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void setFavourite() {
        translate.setFavourite(!translate.isFavourite());
        mRepository.setFavourites(translate);
        mView.changeFavourite(translate.isFavourite());
    }

    @Override
    public void onActivityResult() {

    }

    @Override
    public void swapLanguage() {

    }

    @Override
    public void setSourceLang() {
        translate.setSourceLangCode(mRepository.getSourceCode());
        mView.setSourceLang(mRepository.getSourceName());

    }

    @Override
    public void setTargetLang() {
        translate.setTargetLangCode(mRepository.getTargetCode());
        mView.setTargetLang(mRepository.getTargetName());
    }
}
