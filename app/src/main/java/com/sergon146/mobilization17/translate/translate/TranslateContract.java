package com.sergon146.mobilization17.translate.translate;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import com.sergon146.mobilization17.BasePresenter;
import com.sergon146.mobilization17.BaseView;
import com.sergon146.mobilization17.pojo.Translate;

import java.util.List;

public interface TranslateContract {
    interface View extends BaseView<Presenter> {
        Context getContext();

        /**
         * Set name of source language in top bar
         *
         * @param sourceLang language name
         */
        void setSourceLang(String sourceLang);

        /**
         * Set name of target language in top bar
         *
         * @param targetLang language name
         */
        void setTargetLang(String targetLang);

        /**
         * Hide top bar with languages name
         */
        void hideTopBar();

        /**
         * Show top bar with languages name
         */
        void showTopBar();

        /**
         * Swap source and target languages
         */
        void swapLanguage();

        /**
         * Get current source text in edit text
         *
         * @return source text
         */
        String getSourceText();

        /**
         * Set new tet in source edit text
         *
         * @param sourceText new source text
         */
        void setSourceText(String sourceText);

        /**
         * Hide view with target text
         */
        void hideTargetText();

        /**
         * Show view with target text
         */
        void showTargetText();

        /**
         * Hide view with source speech icon
         */
        void hideSourceSpeechOut();

        /**
         * Start speak out source text
         */
        void sourceSpeakOut();

        /**
         * Show view with source speech icon
         */
        void showSourceSpeechOut();

        /**
         * Set new target text
         *
         * @param targetText new target text
         */
        void setTargetText(String targetText);

        /**
         * Hide layout with target buttons
         */
        void hideTargetButtons();

        /**
         * Show layout with target buttons
         */
        void showTargetButtons();

        /**
         * Start speak out target text
         */
        void speakTargetOut();

        /**
         * Remove all views from mean layout
         */
        void clearMeanLayout();

        /**
         * Set new mean views
         *
         * @param layoutList list of mean views
         */
        void setMean(List<android.view.View> layoutList);

        /**
         * Hide "Connection error" message
         */
        void hideOfflineMessage();

        /**
         * Show "Connection error" message
         */
        void showOfflineMessage();

        /**
         * Hide translate progress bar
         */
        void hideProgress();

        /**
         * Show translate progress bar
         */
        void showProgress();

        /**
         * Set current translate as favourite
         *
         * @param isFavourite is translation favourite
         */
        void changeFavourite(boolean isFavourite);
    }

    interface Presenter extends BasePresenter {
        /**
         * Load translate by text
         *
         * @param text text for translate
         */
        void loadTranslate(String text);

        /**
         * Load dictionary translation
         *
         * @param translate current translate
         */
        void loadWord(Translate translate);

        /**
         * Swap the favourites status of the current translation
         */
        void swapFavourite();

        /**
         * Swap source and target languages
         */
        void swapLanguage();

        /**
         * Set new value of source language name
         */
        void setSourceLang();

        /**
         * Set new value of target language name
         */
        void setTargetLang();

        /**
         * Start speak out source text
         */
        void speakSourceOut(String s, TextToSpeech tts);

        /**
         * Start speak out target text
         */
        void speakTargetOut(String s, TextToSpeech tts);

        /**
         * @return source language code
         */
        String getSourceLangCode();

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
