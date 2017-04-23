package com.sergon146.mobilization17.splash;

import com.sergon146.mobilization17.BasePresenter;
import com.sergon146.mobilization17.BaseView;

public interface SplashContract {
    interface View extends BaseView<Presenter> {
        /**
         * Go to translate activity when loaded lang or connection error
         */
        void finishSplash();

        /**
         * Show Toast with network error text
         */
        void showErrorLoadToast();
    }

    interface Presenter extends BasePresenter {
        //do something only on subscribe
    }
}
