package com.sergon146.mobilization17.data.source;

import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

import rx.Observable;

public interface TranslationDataSource {
    Observable<List<Language>> loadLangs(String localeCode);

    Observable<Translate> loadTranslateSentence(Translate translate);

    Observable<Translate> loadTranslateWord(Translate translate);

    boolean isContainTranslate(Translate translate);

    void saveTranslate(Translate translate);

    Observable<Translate> loadFavourites();

    void clearFavourites();

    void setFavourites(Translate translate);

    Observable<List<Translate>> searchInHistory(String searchText);

    String getSourceCode();

    String getTargetCode();

    String getSourceName();

    String getTargetName();
}
