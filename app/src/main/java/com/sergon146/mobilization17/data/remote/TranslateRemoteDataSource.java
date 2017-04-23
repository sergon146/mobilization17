package com.sergon146.mobilization17.data.remote;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergon146.mobilization17.data.TranslationDataSource;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.service.TranslateService;
import com.sergon146.mobilization17.util.Const;
import com.sergon146.mobilization17.util.Util;

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
    public boolean isEmptyLangList(String localeCode) {
        //only in local
        return true;
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
    public void saveLanguages(String localeCode, List<Language> languages) {
        //only in local
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
        //only in local
        return false;
    }

    @Override
    public void saveTranslate(Translate translate) {
        //only in local
    }

    @Override
    public void deleteTranslate(Translate translate) {
        //only in local
    }

    @Override
    public Observable<List<Translate>> loadHistory() {
        //only in local
        return null;
    }

    @Override
    public Observable<List<Translate>> loadFavourites() {
        //only in local
        return null;
    }

    @Override
    public void clearFavourites() {
        //only in local
    }

    @Override
    public void setFavourites(Translate translate) {
        //only in local
    }

    @Override
    public Observable<List<Translate>> searchInFavourite(String searchText) {
        //only in local
        return null;
    }

    @Override
    public Observable<List<Translate>> searchInHistory(String searchText) {
        //only in local
        return null;
    }

    @Override
    public void clearHistory() {
        //only in local
    }

    @Override
    public void setSourceLang(String sourceCode) {
        //only in local
    }

    @Override
    public void setTargetLang(String targetLang) {
        //only in local
    }

    @Override
    public String getSourceCode() {
        //only in local
        return null;
    }

    @Override
    public String getTargetCode() {
        //only in local
        return null;
    }

    @Override
    public String getSourceName() {
        //only in local
        return null;
    }

    @Override
    public String getTargetName() {
        //only in local
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
            language.setName(entry.getValue());
            languages.add(language);
        }
        languages = Util.sortLangs(languages);
        return languages;
    }
}
