package com.sergon146.mobilization17.data.source;

import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

import rx.Observable;

public class TranslateRepository implements TranslationDataSource {
    private static TranslateRepository instance = null;
    private final TranslationDataSource localSource, remoteSource;

    private TranslateRepository(TranslationDataSource localSource, TranslationDataSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }

    public static TranslateRepository getInstance(TranslationDataSource localSource,
                                                  TranslationDataSource remoteSource) {
        if (instance == null) {
            instance = new TranslateRepository(localSource, remoteSource);
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    @Override
    public Observable<List<Language>> loadLangs(String localeCode) {
        return null;
    }

    @Override
    public Observable<Translate> loadFavourites() {
        return localSource.loadFavourites();
    }

    @Override
    public Observable<List<Translate>> loadTranslateWord() {
        return null;
    }

    @Override
    public Observable<List<Translate>> loadTranslateSentence() {
        return null;
    }

    @Override
    public void clearFavourites() {
        localSource.clearFavourites();
    }

    @Override
    public void setFavourites(Translate translate) {
        localSource.setFavourites(translate);
    }

    @Override
    public Observable<Translate> searchInHistory(String searchText) {
        return localSource.searchInHistory(searchText);
    }
}
