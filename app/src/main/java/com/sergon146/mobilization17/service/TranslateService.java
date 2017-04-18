package com.sergon146.mobilization17.service;

import com.sergon146.mobilization17.pojo.translate.mapper.SentenceMapper;
import com.sergon146.mobilization17.pojo.translate.mapper.WordMapper;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface TranslateService {

    @GET("getLangs?")
    Observable<Map<String, Object>> loadLanguages(@Query("key") String key,
                                                  @Query("ui") String locale);

    @GET("translate?")
    Observable<SentenceMapper> loadTranslateSentences(@Query("key") String key,
                                                      @Query("text") String text,
                                                      @Query("lang") String lang);

    @GET("lookup?")
    Observable<WordMapper> loadTranslateWord(@Query("key") String key,
                                             @Query("lang") String lang,
                                             @Query("text") String text);
}