package com.sergon146.mobilization17.data;

import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

import rx.Observable;

public interface TranslationDataSource {
    /**
     * Check in DB is language list is empty
     *
     * @param localeCode current locale code
     * @return is language list is empty
     */
    boolean isEmptyLangList(String localeCode);

    /**
     * Load Languages from DB or using YaAPI
     *
     * @param localeCode current Locale code
     * @return list of Language
     */
    Observable<List<Language>> loadLangs(String localeCode);

    /**
     * Save in DB list of loaded Languages
     *
     * @param localeCode current Locale code
     * @param languages  list of Languages
     */
    void saveLanguages(String localeCode, List<Language> languages);

    /**
     * Load translation of sentence from DB or Yandex translate API
     *
     * @param translate current source language code, target language code and text
     *                  as Translate object
     * @return Translate with targetText
     */
    Observable<Translate> loadTranslateSentence(Translate translate);

    /**
     * Load translation of sentence from DB or Yandex dictionary API
     *
     * @param translate current source language code, target language code and text
     *                  as Translate object
     * @return Translate with wordMapper and wordJson
     */
    Observable<Translate> loadTranslateWord(Translate translate);

    /**
     * Check is Translate contains in DB
     *
     * @param translate current translate
     * @return is contains in DB
     */
    boolean isContainTranslate(Translate translate);

    /**
     * Save in DB current Translate
     *
     * @param translate current Translate
     */
    void saveTranslate(Translate translate);

    /**
     * Delete translate from DB
     *
     * @param translate current Translate
     */
    void deleteTranslate(Translate translate);

    /**
     * Get all items from History
     *
     * @return all translations in DB
     */
    Observable<List<Translate>> loadHistory();

    /**
     * Search in history where translation contains searchText in source or target text
     *
     * @param searchText search query param
     * @return List of translations containing a searchText
     */
    Observable<List<Translate>> searchInHistory(String searchText);

    /**
     * Delete all translation from History
     */
    void clearHistory();

    /**
     * Set current translate as favourite
     *
     * @param translate current translate
     */
    void setFavourites(Translate translate);

    /**
     * Load all favourite translations from DB
     *
     * @return List of favourite translation
     */
    Observable<List<Translate>> loadFavourites();

    /**
     * Delete all favourites from DB
     */
    void clearFavourites();

    /**
     * Search in favourites where translation contains searchText in source or target text
     *
     * @param searchText search query param
     * @return List of favourite translations containing a searchText
     */
    Observable<List<Translate>> searchInFavourite(String searchText);

    /**
     * Set current lang as source by code
     *
     * @param sourceCode language code
     */
    void setSourceLang(String sourceCode);

    /**
     * Set current lang as source by code
     *
     * @param targetCode language code
     */
    void setTargetLang(String targetCode);

    /**
     * Get code of source language
     *
     * @return language code
     */
    String getSourceCode();

    /**
     * Get code of target language
     *
     * @return language code
     */
    String getTargetCode();

    /**
     * Get name of source language
     *
     * @return language name
     */
    String getSourceName();

    /**
     * Get name of target language
     *
     * @return language name
     */
    String getTargetName();
}
