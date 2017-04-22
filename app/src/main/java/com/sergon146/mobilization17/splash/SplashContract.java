package com.sergon146.mobilization17.splash;

import com.sergon146.mobilization17.BasePresenter;
import com.sergon146.mobilization17.BaseView;

public interface SplashContract {
    interface View extends BaseView<Presenter> {
        void finishSplash();

        void showErrorLoadToast();
    }

    interface Presenter extends BasePresenter {
    }
}
