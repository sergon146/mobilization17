package com.sergon146.mobilization17.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.db.DbBackend;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.presenter.impl.UtilPresenterImpl;
import com.sergon146.mobilization17.presenter.UtilPresenter;

import java.util.List;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        loadLanguagesIfNecessary();
    }

    private void loadLanguagesIfNecessary() {
        DbBackend backend = new DbBackend(getApplicationContext());
        if (backend.isEmptyLocaleLanguageList(Locale.getDefault().getLanguage())) {
            UtilPresenter presenter = new UtilPresenterImpl(this);
            presenter.loadLanguages(Locale.getDefault().getLanguage());
        } else {
            goToTranslateActivity();
        }
    }

    public void setLanguage(List<Language> languages) {
        if (languages != null) {
            DbBackend backend = new DbBackend(getApplicationContext());
            backend.insertLanguages(Locale.getDefault().getLanguage(), languages);
        }

        goToTranslateActivity();
    }

    public void goToTranslateActivity() {
        Intent intent = new Intent(SplashActivity.this, TranslateActivity.class);
        startActivity(intent);
        finish();
    }
}
