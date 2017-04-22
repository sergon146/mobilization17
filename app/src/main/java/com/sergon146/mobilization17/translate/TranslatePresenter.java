package com.sergon146.mobilization17.translate;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sergon146.mobilization17.data.source.TranslateRepository;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.pojo.translate.mapper.WordMapper;
import com.sergon146.mobilization17.util.Const;
import com.sergon146.mobilization17.util.NetworkUtil;
import com.sergon146.mobilization17.util.Util;
import com.sergon146.mobilization17.view.MeanLayout;

import java.util.ArrayList;
import java.util.Locale;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

public class TranslatePresenter implements TranslateContract.Presenter {
    private TranslateRepository mRepository;
    private TranslateContract.View mView;
    private CompositeSubscription mSubscriptions;

    private Translate translate;

    public TranslatePresenter(TranslateRepository repository, TranslateContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mRepository = repository;
        translate = new Translate();
        mSubscriptions = new CompositeSubscription();

        translate.setSourceLangCode(mRepository.getSourceCode());
        translate.setTargetLangCode(mRepository.getTargetCode());
    }

    @Override
    public void subscribe() {
       initialProcess(mView.getContext());
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        initialProcess(context);
    }

    private void initialProcess(Context context) {
        if (NetworkUtil.getConnectivityStatus(context) ==
                NetworkUtil.TYPE_NOT_CONNECTED) {
            mView.showOfflineMessage();
            mView.hideButtons();
            mView.hideTargetText();
        } else {
            loadLanguagesIfNecessary(Locale.getDefault().getLanguage());
            mView.hideOfflineMessage();
            loadTranslate();
        }
    }

    @Override
    public void loadLanguagesIfNecessary(String localeCode) {
        if (mRepository.isEmptyLangList(localeCode)) {
            mView.showProgress();
            mRepository.loadLangs(localeCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(t -> {
                        setSourceLang();
                        setTargetLang();
                        mView.hideProgress();
                    });
        } else {
            setSourceLang();
            setTargetLang();
        }
    }

    private void loadTranslate() {
        loadTranslate(mView.getSourceText());
    }

    @Override
    public void loadTranslate(String text) {
        if (NetworkUtil.getConnectivityStatus(mView.getContext()) ==
                NetworkUtil.TYPE_NOT_CONNECTED) {
            mView.showOfflineMessage();
            return;
        }

        translate.setSourceText(Util.trimAll(text));

        if (translate.getSourceText().isEmpty()) {
            mView.hideSourceSpeechOut();
            return;
        }

        mView.showProgress();
        mView.hideTargetText();
        mView.clearMeanLayout();
        mView.hideButtons();
        mView.hideOfflineMessage();

        translate.setTargetText("");
        translate.setWordJson("");
        translate.setFavourite(false);
        translate.setWordMapper(null);

        mRepository.loadTranslateSentence(translate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Subscriber<Translate>() {
                    @Override
                    public void onNext(Translate tr) {
                        mView.setTargetText(tr.getTargetText());
                        mView.showTargetText();
                        mView.changeFavourite(tr.isFavourite());
                        if (!tr.getTargetText().isEmpty()) {
                            if (Util.isWord(translate.getSourceText())) {
                                if (tr.getWordJson().isEmpty() && tr.getWordMapper() == null) {
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
                        Log.w("Translate", "Error while translated sentence: " + text + " " + e.toString());
                    }
                });

    }

    private void loadWord(Translate translate) {
        mRepository.loadTranslateWord(translate)
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
    }

    @Override
    public void setFavourite() {
        translate.setFavourite(!translate.isFavourite());
        mRepository.setFavourites(translate);
        mView.changeFavourite(translate.isFavourite());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Const.REQUEST_CODE_SOURCE:
                    translate.setSourceLangCode((String) data.getExtras().get(Const.CODE));
                    mRepository.setSourceLang(translate.getSourceLangCode());
                    mView.setSourceLang((String) data.getExtras().get(Const.LANGUAGE));

                    loadTranslate();
                    break;
                case Const.REQUEST_CODE_TARGET:
                    translate.setTargetLangCode((String) data.getExtras().get(Const.CODE));
                    mRepository.setTargetLang(translate.getTargetLangCode());
                    mView.setTargetLang((String) data.getExtras().get(Const.LANGUAGE));

                    loadTranslate();
                    break;
                case Const.REQUEST_CODE_RECOGNITION:
                    ArrayList<String> matches = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    if (!matches.get(0).isEmpty()) {
                        mView.setSourceText(matches.get(0));
                        loadTranslate();
                    }
                    break;
            }
        }
    }

    @Override
    public void swapLanguage() {
        String temp = translate.getSourceLangCode();
        translate.setSourceLangCode(translate.getTargetLangCode());
        translate.setTargetLangCode(temp);

        mRepository.setSourceLang(translate.getSourceLangCode());
        mRepository.setTargetLang(translate.getTargetLangCode());

        setSourceLang();
        setTargetLang();
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
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, Const.SOURCE_SPEECH);
        } else {
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void speakTargetOut(String s, TextToSpeech tts) {
        tts.setLanguage(new Locale(translate.getTargetLangCode()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, Const.TARGET_SPEECH);
        } else {
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public String getSourceLangCode() {
        return translate.getSourceLangCode();
    }

    private void setMeans(WordMapper word) {
        View.OnClickListener flowListener = v -> {
            TextView view = (TextView) v;
            mView.setSourceText(view.getText().toString());
            swapLanguage();
        };
        MeanLayout layout = new MeanLayout(mView.getContext(), flowListener);
        mView.setMean(layout.getMeanViews(word));
    }
}
