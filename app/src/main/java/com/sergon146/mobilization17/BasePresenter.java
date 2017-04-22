package com.sergon146.mobilization17;

import android.content.Context;
import android.content.Intent;

public interface BasePresenter {

    void subscribe();

    void unsubscribe();

    void onReceive(Context context, Intent intent);

    void loadLanguagesIfNecessary(String localeCode);
}