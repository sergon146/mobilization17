package com.sergon146.mobilization17.util;

import android.content.Context;

import com.sergon146.mobilization17.data.TranslateRepository;
import com.sergon146.mobilization17.data.local.TranslateLocalDataSource;
import com.sergon146.mobilization17.data.remote.TranslateRemoteDataSource;
import com.sergon146.mobilization17.pojo.Language;

import java.util.Collections;
import java.util.List;


public class Util {
    /**
     * Get text without excess spaces
     *
     * @param text source text
     * @return trimmed text
     */
    public static String trimAll(String text) {
        return text.replaceAll("[\\s]{2,}", " ").trim();
    }

    /**
     * Check whether the text is a word
     *
     * @param text checked text
     * @return text is a word
     */
    public static boolean isWord(String text) {
        return (text.length() > 0 && text.split("\\s+").length == 1);
    }

    /**
     * Sort language ASC
     *
     * @param languages list of loaded languages
     * @return sorted list
     */
    public static List<Language> sortLangs(List<Language> languages) {
        Collections.sort(languages, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        return languages;
    }

    /**
     * Get repo
     *
     * @param context Context
     * @return instance of repo
     */
    public static TranslateRepository provideTranslateRepository(Context context) {
        assert context != null;
        return TranslateRepository.getInstance(TranslateLocalDataSource.getInstance(context),
                TranslateRemoteDataSource.getInstance());
    }
}
