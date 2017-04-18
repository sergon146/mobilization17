package com.sergon146.mobilization17.util;

import android.util.Log;

import com.sergon146.mobilization17.pojo.Language;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

public class Util {
    public static boolean isWord(String s) {
        return (s.length() > 0 && s.split("\\s+").length == 1);
    }

    public static String getLoadString(String urlLink) {
        try {
            URL url = new URL(urlLink);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return buffer.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    public static String getEncodeText(String text) {
        try {
            return URLEncoder.encode(text, Const.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static ArrayList<Language> sortLangs(ArrayList<Language> languages) {
        Collections.sort(languages, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        return languages;
    }
}
