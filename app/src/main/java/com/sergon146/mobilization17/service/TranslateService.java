package com.sergon146.mobilization17.service;

import com.sergon146.mobilization17.pojo.mapper.SentenceMapper;
import com.sergon146.mobilization17.pojo.mapper.WordMapper;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface TranslateService {

    /**
     * Load the list of supported languages by current Locale
     *
     * @param key    API KEY
     * @param locale current Locale
     * @return Map of String - Object
     */
    @GET("getLangs?")
    Observable<Map<String, Object>> loadLanguages(@Query("key") String key,
                                                  @Query("ui") String locale);

    /**
     * Load a sentence translation from the Yandex translate API
     *
     * @param key  API KEY
     * @param text text for translation
     * @param lang string as {source language code}-{target language code}
     * @return Object with sentence translation which mapped from json
     */
    @GET("translate?")
    Observable<SentenceMapper> loadTranslateSentences(@Query("key") String key,
                                                      @Query("text") String text,
                                                      @Query("lang") String lang);

    /**
     * Load a word translation from the Yandex dictionary API
     *
     * @param key  API KEY
     * @param lang string as {source language code}-{target language code}
     * @param text word for translate
     * @return Object with word translation which mapped from json
     */
    @GET("lookup?")
    Observable<WordMapper> loadTranslateWord(@Query("key") String key,
                                             @Query("lang") String lang,
                                             @Query("text") String text);
}