package com.sergon146.mobilization17.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.activity.TranslateActivity;
import com.sergon146.mobilization17.util.NetworkUtil;
import com.sergon146.mobilization17.util.Util;

public class SplashActivity extends AppCompatActivity implements SplashContract.View {
    private SplashContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        if (NetworkUtil.getConnectivityStatus(getApplicationContext()) == NetworkUtil.TYPE_NOT_CONNECTED) {
            showErrorLoadToast();
            finishSplash();
        } else {
            new SplashPresenter(this, Util.provideTasksRepository(getApplicationContext()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(SplashContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSplash() {
        Intent intent = new Intent(SplashActivity.this, TranslateActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showErrorLoadToast() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.offline_message),
                Toast.LENGTH_SHORT).show();
    }
}
