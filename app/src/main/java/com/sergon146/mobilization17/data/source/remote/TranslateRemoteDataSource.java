package com.sergon146.mobilization17.data.source.remote;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergon146.mobilization17.data.source.TranslationDataSource;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.service.TranslateService;
import com.sergon146.mobilization17.util.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class TranslateRemoteDataSource implements TranslationDataSource {
    private static TranslateRemoteDataSource instance;

    public static TranslateRemoteDataSource getInstance() {
        if (instance == null) {
            instance = new TranslateRemoteDataSource();
        }
        return instance;
    }

    @Override
    public Observable<List<Language>> loadLangs(String localeCode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.TRANSLATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        TranslateService translateService = retrofit.create(TranslateService.class);

        return translateService.loadLanguages(Const.TRANSLATE_API_KEY, localeCode)
                .doOnError(th -> Log.e(Const.LOG_TAG, th.toString()))
                .map(this::getLangsListFromMap);
    }

    @Override
    public Observable<Translate> loadTranslateSentence(Translate translate) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.TRANSLATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(TranslateService.class)
                .loadTranslateSentences(Const.TRANSLATE_API_KEY, translate.getSourceText(),
                        translate.getSourceLangCode() + "-" + translate.getTargetLangCode())
                .doOnError(th -> Log.e(Const.LOG_TAG, th.toString()))
                .map(st -> st.getText().get(0))
                .map(s -> {
                    translate.setTargetText(s);
                    return translate;
                });
    }

    @Override
    public Observable<Translate> loadTranslateWord(Translate translate) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.DICTIONARY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();


        return retrofit.create(TranslateService.class)
                .loadTranslateWord(Const.DICTIONARY_API_KEY,
                        translate.getSourceLangCode() + "-" + translate.getTargetLangCode(),
                        translate.getSourceText())
                .doOnError(th -> Log.e(Const.LOG_TAG, th.toString()))
                .map(wr -> {
                    translate.setWordMapper(wr);
                    return wr;
                })
                .map(wr -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        translate.setWordJson(mapper.writeValueAsString(wr));
                    } catch (IOException e) {
                        translate.setWordJson("");
                    }
                    return translate;
                });
    }

    @Override
    public boolean isContainTranslate(Translate translate) {
        return false;
    }

    @Override
    public void saveTranslate(Translate translate) {
    }

    @Override
    public Observable<Translate> loadFavourites() {
        return null;
    }

    @Override
    public void clearFavourites() {
    }

    @Override
    public void setFavourites(Translate translate) {
    }

    @Override
    public Observable<List<Translate>> searchInHistory(String searchText) {
        return null;
    }

    @Override
    public void setSourceLang(String sourceCode) {

    }

    @Override
    public void setTargetLang(String targetLang) {

    }

    @Override
    public String getSourceCode() {
        return null;
    }

    @Override
    public String getTargetCode() {
        return null;
    }

    @Override
    public String getSourceName() {
        return null;
    }

    @Override
    public String getTargetName() {
        return null;
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
