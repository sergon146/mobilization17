package com.sergon146.mobilization17.translate;

import android.content.Context;
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

        void hideTargetText();

        void showTargetText();

        void hideSourceSpeechOut();

        void speakSourceOut();

        void showSourceSpeechOut();

        void setTargetText(String targetText);

        void hideButtons();

        void showButtons();

        void speakTargetOut();

        void hideMean();

        void setMean(List<android.view.View> layoutList);

        void hideConnectionError();

        void showConnectionError();

        void hideProgress();

        void showProgress();

        void toastError();

        void changeFavourite(boolean isFavourite);
    }

    interface Presenter extends BasePresenter {
        void loadTranslate(String text);

        void setFavourite();

        void onActivityResult();

        void swapLanguage();

        void setSourceLang();

        void setTargetLang();

        void speakSourceOut(String s, TextToSpeech tts);

        void speakTargetOut(String s, TextToSpeech tts);
    }
}
