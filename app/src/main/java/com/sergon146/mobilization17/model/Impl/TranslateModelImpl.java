package com.sergon146.mobilization17.model.Impl;

import com.sergon146.mobilization17.model.TranslateModel;
import com.sergon146.mobilization17.pojo.translate.mapper.SentenceMapper;
import com.sergon146.mobilization17.pojo.translate.mapper.WordMapper;
import com.sergon146.mobilization17.service.TranslateService;
import com.sergon146.mobilization17.util.Const;

import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class TranslateModelImpl implements TranslateModel {

    @Override
    public Observable<Map<String, Object>> loadLanguages(String localeCode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.TRANSLATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        TranslateService translateService = retrofit.create(TranslateService.class);
        return translateService.loadLanguages(Const.TRANSLATE_API_KEY, localeCode);
    }

    @Override
    public Observable<WordMapper> loadTranslateWord(String text, String source, String target) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.DICTIONARY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(TranslateService.class)
                .loadTranslateWord(Const.DICTIONARY_API_KEY, source + "-" + target, text);
    }

    @Override
    public Observable<SentenceMapper> loadTranslateSentence(String text, String source, String target) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.TRANSLATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(TranslateService.class)
                .loadTranslateSentences(Const.TRANSLATE_API_KEY, text, source + "-" + target);
    }
}
