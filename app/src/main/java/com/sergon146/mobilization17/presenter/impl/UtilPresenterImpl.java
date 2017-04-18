package com.sergon146.mobilization17.presenter.impl;

import android.util.Log;

import com.sergon146.mobilization17.activity.SplashActivity;
import com.sergon146.mobilization17.model.Impl.TranslateModelImpl;
import com.sergon146.mobilization17.model.TranslateModel;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.presenter.UtilPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UtilPresenterImpl implements UtilPresenter {
    private TranslateModel translateModel;
    private ArrayList<Language> languages;
    private SplashActivity activity;

    public UtilPresenterImpl(SplashActivity activity) {
        this.activity = activity;
        translateModel = new TranslateModelImpl();
        languages = new ArrayList<>();
    }

    @Override
    public void loadLanguages(String localeCode) {
        translateModel.loadLanguages(localeCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .map(this::getLangsListFromMap)
                .subscribe(new Subscriber<List<Language>>() {
                    @Override
                    public void onNext(List<Language> langs) {
                        activity.setLanguage(langs);
                    }

                    @Override
                    public void onCompleted() {
                        Log.i("Mobilization", "Completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        activity.setLanguage(null);
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
        languages = new ArrayList<>();
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
