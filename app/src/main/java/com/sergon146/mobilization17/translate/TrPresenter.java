package com.sergon146.mobilization17.translate;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sergon146.mobilization17.data.source.TranslateRepository;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.pojo.translate.mapper.WordMapper;
import com.sergon146.mobilization17.util.MeanLayout;
import com.sergon146.mobilization17.util.Util;

import java.util.Locale;

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
        mView = view;
        mView.setPresenter(this);
        mRepository = repository;
        translate = new Translate();
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
        mView.hideButtons();

        translate.setSourceText(Util.trimAll(text));
        translate.setTargetText("");
        translate.setWordJson("");
        translate.setFavourite(false);
        translate.setWordMapper(null);
        translate.setSourceLangCode(mRepository.getSourceCode());
        translate.setTargetLangCode(mRepository.getTargetCode());

        Subscription subscription = mRepository.loadTranslateSentence(translate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Subscriber<Translate>() {
                    @Override
                    public void onNext(Translate tr) {
                        mView.setTargetText(tr.getTargetText());
                        mView.changeFavourite(tr.isFavourite());
                        if (!tr.getTargetText().isEmpty()) {
                            if (Util.isWord(text)) {
                                if (tr.getWordJson().isEmpty()) {
                                    loadWord(tr);
                                } else {
                                    setMeans(tr.getWordMapper());
                                }
                            } else {
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
                        setMeans(tr.getWordMapper());
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
                        translate.setWordMapper(null);
                        translate.setWordJson("");
                        mRepository.saveTranslate(translate);

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
        String temp = translate.getSourceLangCode();
        translate.setSourceLangCode(translate.getTargetLangCode());
        translate.setTargetLangCode(temp);

        mRepository.setSourceLang(translate.getSourceLangCode());
        mRepository.setTargetLang(translate.getTargetLangCode());

        mView.setSourceLang(mRepository.getSourceName());
        mView.setTargetLang(mRepository.getTargetName());
    }

    @Override
    public void setSourceLang() {
        mView.setSourceLang(mRepository.getSourceName());

    }

    @Override
    public void setTargetLang() {
        mView.setTargetLang(mRepository.getTargetName());
    }

    @Override
    public void speakSourceOut(String s, TextToSpeech tts) {
        tts.setLanguage(new Locale(translate.getSourceLangCode()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void speakTargetOut(String s, TextToSpeech tts) {
        tts.setLanguage(new Locale(translate.getTargetLangCode()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void setMeans(WordMapper word) {
        FlowItemListener flowListener = v -> {
            TextView view = (TextView) v;
            mView.setSourceText(view.getText().toString());
            swapLanguage();
        };
        MeanLayout layout = new MeanLayout(mView.getContext(), flowListener);
        mView.setMean(layout.getMeanViews(word));
    }

    public interface FlowItemListener {
        void onFlowClick(View v);
    }
}
