package com.sergon146.mobilization17.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sergon146.mobilization17.data.source.TranslationDataSource;
import com.sergon146.mobilization17.db.DbBackend;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.pojo.translate.mapper.SentenceMapper;
import com.sergon146.mobilization17.pojo.translate.mapper.WordMapper;

import java.util.List;
import java.util.Locale;

import rx.Observable;


public class TranslateLocalDataSource implements TranslationDataSource {
    private static TranslateLocalDataSource instance;
    private DbBackend backend;

    public TranslateLocalDataSource(@NonNull Context context) {
        backend = new DbBackend(context);
    }

    public static TranslateLocalDataSource getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new TranslateLocalDataSource(context);
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
        return Observable.from(backend.getFavourites());
    }

    @Override
    public Observable<WordMapper> loadTranslateWord(Translate translate) {
        return null;
    }

    @Override
    public boolean isContainTranslate(Translate translate) {
        return backend.isTranslateContains(translate);
    }

    @Override
    public void saveTranslate(Translate translate) {
        backend.saveTranslate(translate);
    }

    @Override
    public Observable<SentenceMapper> loadTranslateSentence(Translate translate) {
        return null;
    }

    @Override
    public void clearFavourites() {
        backend.clearFavourites();
    }

    @Override
    public void setFavourites(Translate translate) {
        backend.updateTranslateFavourite(translate);
    }

    @Override
    public Observable<Translate> searchInHistory(String searchText) {
        return Observable.from(backend.searchInFavourite(searchText));
    }

    @Override
    public String getSourceCode() {
        return backend.getSourceCode();
    }

    @Override
    public String getTargetCode() {
        return backend.getTargetCode();
    }

    @Override
    public String getSourceName() {
        return backend.getSourceName(Locale.getDefault().getLanguage());
    }

    @Override
    public String getTargetName() {
        return backend.getTargetName(Locale.getDefault().getLanguage());
    }
}
