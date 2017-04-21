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
    public Observable<Translate> loadTranslateWord(Translate translate) {
        if (localSource.isContainTranslate(translate)) {
            return localSource.loadTranslateWord(translate);
        } else {
            return remoteSource.loadTranslateWord(translate);
        }
    }

    @Override
    public boolean isContainTranslate(Translate translate) {
        return false;
    }

    @Override
    public Observable<Translate> loadTranslateSentence(Translate translate) {
        if (localSource.isContainTranslate(translate)) {
            return localSource.loadTranslateSentence(translate);
        } else {
            return remoteSource.loadTranslateSentence(translate);
        }
    }

    @Override
    public void saveTranslate(Translate translate) {
        localSource.saveTranslate(translate);
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
    public Observable<List<Translate>> searchInHistory(String searchText) {
        return localSource.searchInHistory(searchText);
    }

    @Override
    public String getSourceCode() {
        return localSource.getSourceCode();
    }

    @Override
    public String getTargetCode() {
        return localSource.getTargetCode();
    }

    @Override
    public String getSourceName() {
        return localSource.getSourceName();
    }

    @Override
    public String getTargetName() {
        return localSource.getTargetName();
    }
}
