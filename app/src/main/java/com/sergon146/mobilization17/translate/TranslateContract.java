package com.sergon146.mobilization17.translate;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import com.sergon146.mobilization17.BasePresenter;
import com.sergon146.mobilization17.BaseView;

import java.util.List;

public interface TranslateContract {
    interface View extends BaseView<Presenter> {
        Context getContext();

        void setSourceLang(String sourceLang);

        void setTargetLang(String targetLang);

        void swapLanguage();

        void setSourceText(String sourceText);

        String getSourceText();

        void hideTargetText();

        void showTargetText();

        String getTargetText();

        void hideSourceSpeechOut();

        void speakSourceOut();

        void showSourceSpeechOut();

        void setTargetText(String targetText);

        void hideButtons();

        void showButtons();

        void speakTargetOut();

        void clearMeanLayout();

        void setMean(List<android.view.View> layoutList);

        void hideOfflineMessage();

        void showOfflineMessage();

        void hideProgress();

        void showProgress();

        void changeFavourite(boolean isFavourite);
    }

    interface Presenter extends BasePresenter {
        void loadTranslate(String text);

        void setFavourite();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void swapLanguage();

        void setSourceLang();

        void setTargetLang();

        void speakSourceOut(String s, TextToSpeech tts);

        void speakTargetOut(String s, TextToSpeech tts);

        String getSourceLangCode();
    }
}
