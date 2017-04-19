package com.sergon146.mobilization17.presenter;

import android.content.Intent;
import android.text.TextWatcher;

public interface TranslateFragmentPresenter {
    void doTranslate(String text);

    void setTargetText(String text);

    void setTargetMean();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    String getSourceName();

    String getTargetName();

    void swapLanguages();

    String getSourceCode();

    String getTargetCode();

    TextWatcher getSourceTextWatcher();

    int getImageAndUpdateFavourite();
}
