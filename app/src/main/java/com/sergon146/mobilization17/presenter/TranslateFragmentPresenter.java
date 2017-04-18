package com.sergon146.mobilization17.presenter;

import android.content.Intent;
import android.text.TextWatcher;

public interface TranslateFragmentPresenter {
    void doTranslate(String text);

    void setTranslateSentence(String text);

    void setTranslateWord();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    String getSourceName();

    String getTargetName();

    void swapLanguages();

    String getSourceCode();

    String getTargetCode();

    TextWatcher getSourceTextWatcher();

    void updateTranslateFavourite();
}
