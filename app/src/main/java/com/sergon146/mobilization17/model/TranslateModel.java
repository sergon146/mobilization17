package com.sergon146.mobilization17.model;

import com.sergon146.mobilization17.pojo.translate.mapper.SentenceMapper;
import com.sergon146.mobilization17.pojo.translate.mapper.WordMapper;

import java.util.Map;

import rx.Observable;

public interface TranslateModel {
    Observable<Map<String, Object>> loadLanguages(String localeCode);

    Observable<WordMapper> loadTranslateWord(String text, String source, String target);

    Observable<SentenceMapper> loadTranslateSentence(String text, String source, String target);
}
