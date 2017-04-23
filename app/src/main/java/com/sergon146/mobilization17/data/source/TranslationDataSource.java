package com.sergon146.mobilization17.data.source;

import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

import rx.Observable;

public interface TranslationDataSource {
    boolean isEmptyLangList(String localeCode);

    Observable<List<Language>> loadLangs(String localeCode);

    void saveLanguages(String localeCode, List<Language> languages);

    Observable<Translate> loadTranslateSentence(Translate translate);

    Observable<Translate> loadTranslateWord(Translate translate);

    boolean isContainTranslate(Translate translate);

    void saveTranslate(Translate translate);

    void deleteTranslate(Translate translate);

    Observable<List<Translate>> loadHistory();

    Observable<List<Translate>> searchInHistory(String searchText);

    void clearHistory();

    void setFavourites(Translate translate);

    Observable<List<Translate>> loadFavourites();

    void clearFavourites();

    Observable<List<Translate>> searchInFavourite(String searchText);

    void setSourceLang(String sourceCode);

    void setTargetLang(String targetLang);

    String getSourceCode();

    String getTargetCode();

    String getSourceName();

    String getTargetName();
}
