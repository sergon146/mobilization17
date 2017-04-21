package com.sergon146.mobilization17.translate;

import android.support.v4.view.ViewGroupCompat;

import com.sergon146.mobilization17.BasePresenter;
import com.sergon146.mobilization17.BaseView;

public interface TranslateContract {
    interface View extends BaseView<Presenter> {
        void setSourceLang(String sourceLang);

        void setTargetLang(String targetLang);

        void swapLanguage();

        void setSourceText(String sourceText);

        void hideSourceSpeechOut();

        void speakSourceOut();

        void showSourceSpeechOut();

        void setTargetText(String targetText);

        void hideButtons();

        void showButtons();

        void speakTargetOut();

        void hideMean();

        void setMean(ViewGroupCompat viewGroup);

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

    }
}
