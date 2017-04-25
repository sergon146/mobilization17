package com.sergon146.mobilization17.translate.translate;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sergon146.mobilization17.data.TranslateRepository;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.pojo.mapper.WordMapper;
import com.sergon146.mobilization17.util.Const;
import com.sergon146.mobilization17.util.NetworkUtil;
import com.sergon146.mobilization17.util.Util;
import com.sergon146.mobilization17.view.MeanLayout;

import java.util.ArrayList;
import java.util.List;
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
    private String locale;

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
        locale = Locale.getDefault().getLanguage();
        initialProcess(mView.getContext());
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        locale = Locale.getDefault().getLanguage();
        initialProcess(context);
    }

    private void initialProcess(Context context) {
        if (NetworkUtil.isLostConnection(context)) {
            mView.showOfflineMessage();
            mView.hideTargetButtons();
            mView.hideTargetText();
            mView.hideProgress();
            if (mRepository.isEmptyLangList(locale)) {
                mView.hideTopBar();
            } else {
                setSourceLang();
                setTargetLang();
            }
        } else {
            loadLanguagesIfNecessary(locale);
            mView.hideOfflineMessage();
            loadTranslate();
        }
    }

    @Override
    public void loadLanguagesIfNecessary(String localeCode) {
        if (mRepository.isEmptyLangList(localeCode)) {
            mView.hideTopBar();
            mView.showProgress();
            mRepository.loadLangs(localeCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Language>>() {
                        @Override
                        public void onNext(List<Language> languageList) {
                            mView.showTopBar();
                            setSourceLang();
                            setTargetLang();
                            mView.hideProgress();
                        }

                        @Override
                        public void onCompleted() {
                            loadTranslate();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(Const.LOG_TRANSLATE, "Load languages: " + e);
                        }
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
        if (NetworkUtil.isLostConnection(mView.getContext())) {
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
        mView.hideTargetButtons();
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
                                    mView.hideProgress();
                                    mView.showTargetButtons();
                                    setMeans(tr.getWordMapper());
                                }
                            } else {
                                mView.hideProgress();
                                mView.showTargetButtons();
                                mRepository.saveTranslate(tr);
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {
                        mView.changeFavourite(translate.isFavourite());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.showTargetButtons();
                        Log.e(Const.LOG_TRANSLATE, "Error while translated sentence: " + e);
                    }
                });

    }

    @Override

    public void loadWord(Translate translate) {
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
                        mView.showTargetButtons();
                        mView.changeFavourite(translate.isFavourite());
                        mRepository.saveTranslate(translate);
                    }

                    @Override
                    public void onError(Throwable e) {
                        translate.setWordMapper(null);
                        translate.setWordJson("");
                        mRepository.saveTranslate(translate);
                        mView.showTargetButtons();
                        mView.hideProgress();
                        Log.e(Const.LOG_TRANSLATE, "Error while translated word: " + e);
                    }
                });
    }

    @Override
    public void swapFavourite() {
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
                    String sourceCode = (String) data.getExtras().get(Const.CODE);
                    assert sourceCode != null;
                    if (sourceCode.equals(translate.getTargetLangCode())) {
                        swapLanguage();
                    }
                    translate.setSourceLangCode(sourceCode);
                    mRepository.setSourceLang(translate.getSourceLangCode());
                    mView.setSourceLang((String) data.getExtras().get(Const.LANGUAGE));
                    loadTranslate();
                    break;

                case Const.REQUEST_CODE_TARGET:
                    String targetCode = (String) data.getExtras().get(Const.CODE);
                    assert targetCode != null;
                    if (targetCode.equals(translate.getSourceLangCode())) {
                        swapLanguage();
                    }
                    translate.setTargetLangCode(targetCode);
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
        String sourceName = mRepository.getSourceName();
        if (!sourceName.isEmpty()) {
            mView.setSourceLang(mRepository.getSourceName());
        }
    }

    @Override
    public void setTargetLang() {
        String targetName = mRepository.getTargetName();
        if (!targetName.isEmpty()) {
            mView.setTargetLang(targetName);
        }
    }

    @Override
    public void speakSourceOut(String s, TextToSpeech tts) {
        tts.setLanguage(new Locale(translate.getSourceLangCode()));
        tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, Const.SOURCE_SPEECH);
    }

    @Override
    public void speakTargetOut(String s, TextToSpeech tts) {
        tts.setLanguage(new Locale(translate.getTargetLangCode()));
        tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, Const.TARGET_SPEECH);
    }

    @Override
    public String getSourceLangCode() {
        return translate.getSourceLangCode();
    }

    /**
     * Set mean view by word
     *
     * @param word word mapper
     */
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
