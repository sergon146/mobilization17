package com.sergon146.mobilization17.presenter.impl;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.data.source.local.DbBackend;
import com.sergon146.mobilization17.fragment.TranslateFragment;
import com.sergon146.mobilization17.model.Impl.TranslateModelImpl;
import com.sergon146.mobilization17.model.TranslateModel;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.pojo.translate.mapper.WordMapper;
import com.sergon146.mobilization17.presenter.TranslateFragmentPresenter;
import com.sergon146.mobilization17.util.Const;
import com.sergon146.mobilization17.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import static android.app.Activity.RESULT_OK;

public class TranslateFragmentPresenterImpl implements TranslateFragmentPresenter {
    private TranslateFragment fragment;
    private Subscription subscription = Subscriptions.empty();
    private TranslateModel translateModel;
    private String sourceCode, targetCode;
    private DbBackend backend;
    private Translate translate;

    public TranslateFragmentPresenterImpl(TranslateFragment fragment) {
        this.fragment = fragment;
        translateModel = new TranslateModelImpl();
        backend = new DbBackend(fragment.getContext());
        sourceCode = backend.getSourceCode();
        targetCode = backend.getTargetCode();
        backend = new DbBackend(fragment.getContext());
        if (backend.isEmptyLocaleLanguageList(Locale.getDefault().getLanguage())) {
            loadLanguages(Locale.getDefault().getLanguage());
        }
    }

    public void setSourceLang(String source) {
        backend.updateSource(source);
    }

    public void setTargetLang(String target) {
        backend.setTargetLang(target);
    }

    @Override
    public void doTranslate(String text) {
        translate = new Translate();
        translate.setSourceText(text);
        translate.setSourceLangCode(sourceCode);
        translate.setTargetLangCode(targetCode);
        translateModel.loadTranslateSentence(text, sourceCode, targetCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .cache()
                .onErrorReturn(null)
                .map(st -> st.getText().get(0))
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        if (!fragment.getSourceText().isEmpty()) {
                            translate.setTargetText(s);
                            setTargetText(s);

                            if (Util.isWord(text) && !translate.getTargetText().isEmpty()) {
                                loadWord(translate);
                            } else {
                                translate.setWordJson("");
                                if (!translate.getTargetText().isEmpty()) {
                                    backend.saveTranslate(translate);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {
                        fragment.hideTranslateProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        fragment.hideTranslateProgress();
                        Log.w("Translate", "Error while translated sentence: " + text);
                    }
                });
    }

    private void loadWord(Translate translate) {
        translateModel.loadTranslateWord(translate.getSourceText(), sourceCode, targetCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .onErrorReturn(null)
                .subscribe(new Subscriber<WordMapper>() {
                    @Override
                    public void onNext(WordMapper word) {
                        translate.setWordMapper(word);

                        writeWordJson(word);

                        backend.saveTranslate(translate);
                    }

                    @Override
                    public void onCompleted() {
                        Log.i("Translate", "Translate sentence: " + translate.getSourceText() + " completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        fragment.hideTranslateProgress();
                        Log.w("Dictionary", "Error while translated sentence: " + translate.getSourceText() + " - " + e);
                    }
                });
    }


    @Override
    public void setTargetText(String text) {
        fragment.setTargetText(text);
    }

    @Override
    public void setTargetMean() {
        // TODO: 18.04.2017 in mean layout
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case Const.REQUEST_CODE_SOURCE:
                    sourceCode = data.getStringExtra(Const.LANGUAGE);
                    setSourceLang(sourceCode);
                    fragment.updateSourceBar(getSourceName());
                    break;
                case Const.REQUEST_CODE_TARGET:
                    targetCode = data.getStringExtra(Const.LANGUAGE);
                    setTargetLang(targetCode);
                    fragment.updateTargetBar(getTargetName());
                    break;
                default:
                    return;

            }
            doTranslate(fragment.getSourceText());
        }
    }

    @Override
    public String getSourceName() {
        return backend.getSourceName(Locale.getDefault().getLanguage());
    }

    @Override
    public String getTargetName() {
        return backend.getTargetName(Locale.getDefault().getLanguage());
    }

    @Override
    public void swapLanguages() {
        String temp = fragment.getTargetLanguageText();
        fragment.updateTargetBar(fragment.getSourceLanguageText());
        fragment.updateSourceBar(temp);

        temp = sourceCode;
        sourceCode = targetCode;
        targetCode = temp;

        setSourceLang(sourceCode);
        setTargetLang(targetCode);
    }

    @Override
    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public String getTargetCode() {
        return targetCode;
    }

    @Override
    public TextWatcher getSourceTextWatcher() { //todo in presenter
        return new TextWatcher() {
            CountDownTimer timer = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                ImageView sourceSpeech =
                        (ImageView) fragment.getTranslateLayout().findViewById(R.id.source_speech_view);

                if (s.toString().equals("")) {
                    sourceSpeech.setVisibility(View.INVISIBLE);
                    fragment.setTargetText("");
                } else {
                    sourceSpeech.setVisibility(View.VISIBLE);
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new CountDownTimer(500, 1) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            fragment.showTranslateProgress();
                            doTranslate(s.toString());
                        }
                    }.start();
                }
            }

        };
    }

    @Override
    public int getImageAndUpdateFavourite() {
        translate.setFavourite(!translate.isFavourite());
        backend.updateTranslateFavourite(translate);
        return translate.isFavourite() ?
                R.drawable.ic_active_favourite :
                R.drawable.ic_inactive_favourite;
    }

    private void writeWordJson(WordMapper word) {
        translate.setWordJson(getWordJson(word));
    }

    private String getWordJson(WordMapper word) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(word);
        } catch (IOException e) {
            return "";
        }
    }


    private void loadLanguages(String localeCode) {
        TranslateModel translateModel = new TranslateModelImpl();
        translateModel.loadLanguages(localeCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .map(this::getLangsListFromMap)
                .subscribe(new Subscriber<List<Language>>() {
                    @Override
                    public void onNext(List<Language> langs) {
                        if (langs != null) {
                            backend.insertLanguages(Locale.getDefault().getLanguage(), langs);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        Log.i("Mobilization", "Completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("Mobilization", "Error!");
                    }
                });
    }

    private List<Language> getLangsListFromMap(Map<String, Object> map) {
        Map<String, String> langs = new HashMap<>();
        try {
            langs.putAll((Map<String, String>) map.get("langs"));
        } catch (Exception e) {
            Log.w("ERROR", e);
        }

        int id = 0;
        List<Language> languages = new ArrayList<>();
        for (Map.Entry<String, String> entry : langs.entrySet()) {
            Language language = new Language();
            language.setId(id++);
            language.setCode(entry.getKey());
            if (language.getCode().equals(Locale.getDefault().getLanguage())) {
                language.setSource(true);
                language.setTarget(true);
            }
            language.setName(entry.getValue());
            languages.add(language);
        }

        return languages;
    }
}
