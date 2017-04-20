package com.sergon146.mobilization17.data.source;

import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

import rx.Observable;

public interface TranslationDataSource {
    Observable<List<Language>> loadLangs(String localeCode);

    Observable<Translate> loadFavourites();

    Observable<List<Translate>> loadTranslateWord();

    Observable<List<Translate>> loadTranslateSentence();

    void clearFavourites();

    void setFavourites(Translate translate);

    Observable<Translate> searchInHistory(String searchText);
}
