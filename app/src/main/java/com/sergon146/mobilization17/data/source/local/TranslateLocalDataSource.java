package com.sergon146.mobilization17.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sergon146.mobilization17.data.source.TranslationDataSource;
import com.sergon146.mobilization17.db.DbBackend;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

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
    public Observable<List<Translate>> loadTranslateWord() {
        return null;
    }

    @Override
    public Observable<List<Translate>> loadTranslateSentence() {
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
}
