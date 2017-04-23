package com.sergon146.mobilization17.util;

import android.content.Context;

import com.sergon146.mobilization17.data.TranslateRepository;
import com.sergon146.mobilization17.data.local.TranslateLocalDataSource;
import com.sergon146.mobilization17.data.remote.TranslateRemoteDataSource;
import com.sergon146.mobilization17.pojo.Language;

import java.util.Collections;
import java.util.List;


public class Util {
    public static String trimAll(String text) {
        return text.replaceAll("[\\s]{2,}", " ").trim();
    }

    public static boolean isWord(String s) {
        return (s.length() > 0 && s.split("\\s+").length == 1);
    }


    public static List<Language> sortLangs(List<Language> languages) {
        Collections.sort(languages, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        return languages;
    }

    public static TranslateRepository provideTasksRepository(Context context) {
        assert context != null;
        return TranslateRepository.getInstance(TranslateLocalDataSource.getInstance(context),
                TranslateRemoteDataSource.getInstance());
    }
}
