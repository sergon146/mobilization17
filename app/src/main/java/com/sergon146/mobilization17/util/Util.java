package com.sergon146.mobilization17.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sergon146.mobilization17.data.source.TranslateRepository;
import com.sergon146.mobilization17.data.source.local.TranslateLocalDataSource;
import com.sergon146.mobilization17.data.source.remote.TranslateRemoteDataSource;
import com.sergon146.mobilization17.pojo.Language;

import java.util.ArrayList;
import java.util.Collections;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class Util {
    public static boolean isWord(String s) {
        return (s.length() > 0 && s.split("\\s+").length == 1);
    }

    public static ArrayList<Language> sortLangs(ArrayList<Language> languages) {
        Collections.sort(languages, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        return languages;
    }


    public static TranslateRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        return TranslateRepository.getInstance(TranslateLocalDataSource.getInstance(context),
                TranslateRemoteDataSource.getInstance());
    }

    public static String trimAll(String text) {
        return text.replaceAll("[\\s]{2,}", " ").trim();
    }
}
