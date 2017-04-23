package com.sergon146.mobilization17.data.source;

import android.util.Log;

import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.util.Const;

import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    @Override
    public boolean isEmptyLangList(String localeCode) {
        return localSource.isEmptyLangList(localeCode);
    }

    @Override
    public Observable<List<Language>> loadLangs(String localeCode) {
        Observable<List<Language>> listObservable = remoteSource.loadLangs(localeCode);
        listObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Language>>() {
                    @Override
                    public void onNext(List<Language> languageList) {
                        saveLanguages(Locale.getDefault().getLanguage(), languageList);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(Const.LOG_TAG, "Error " + e);
                    }
                });
        return listObservable;
    }

    @Override
    public void saveLanguages(String localeCode, List<Language> languages) {
        localSource.saveLanguages(localeCode, languages);
    }

    @Override
    public Observable<List<Translate>> loadFavourites() {
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
        return localSource.isContainTranslate(translate);
    }

    @Override
    public Observable<Translate> loadTranslateSentence(Translate translate) {
        if (isContainTranslate(translate)) {
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
    public void deleteTranslate(Translate translate) {
        localSource.deleteTranslate(translate);
    }

    @Override
    public Observable<List<Translate>> loadHistory() {
        return localSource.loadHistory();
    }

    @Override
    public Observable<List<Translate>> searchInHistory(String searchText) {
        return localSource.searchInHistory(searchText);
    }

    @Override
    public void clearHistory() {
        localSource.clearHistory();
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
    public Observable<List<Translate>> searchInFavourite(String searchText) {
        return localSource.searchInFavourite(searchText);
    }

    @Override
    public void setSourceLang(String sourceCode) {
        localSource.setSourceLang(sourceCode);
    }

    @Override
    public void setTargetLang(String targetLang) {
        localSource.setTargetLang(targetLang);
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
