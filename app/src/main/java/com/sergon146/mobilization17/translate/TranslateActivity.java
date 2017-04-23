package com.sergon146.mobilization17.translate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sergon146.mobilization17.BasePresenter;
import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.translate.history.favourite.FavouriteFragment;
import com.sergon146.mobilization17.translate.history.favourite.FavouritePresenter;
import com.sergon146.mobilization17.translate.history.history.HistoryFragment;
import com.sergon146.mobilization17.translate.history.history.HistoryPresenter;
import com.sergon146.mobilization17.translate.translate.TranslateFragment;
import com.sergon146.mobilization17.translate.translate.TranslatePresenter;
import com.sergon146.mobilization17.util.Const;
import com.sergon146.mobilization17.util.Util;

public class TranslateActivity extends AppCompatActivity {
    private BasePresenter mPresenter;
    private BottomNavigationView navigation;
    private int prevItem = 0;
    private int currentItem = 0;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.onReceive(context, intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentItem = savedInstanceState.getInt(Const.PAGE_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNavBar();
        navigation.setSelectedItemId(currentItem);
        setCurrentFragmentByItemId(navigation.getSelectedItemId());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Const.PAGE_ID, currentItem);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initNavBar() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(currentItem);
        navigation.setOnNavigationItemSelectedListener(item -> {
            setCurrentFragmentByItemId(item.getItemId());
            item.setChecked(true);
            return true;
        });
    }

    public void setCurrentFragmentByItemId(int itemId) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        switch (itemId) {
            case R.id.navigation_translate:
                TranslateFragment translateFragment;
                if (fragment == null || !(fragment instanceof TranslateFragment)) {
                    translateFragment = TranslateFragment.newInstance();
                } else {
                    translateFragment = (TranslateFragment) fragment;
                }
                mPresenter = new TranslatePresenter(Util.provideTranslateRepository(getApplicationContext()), translateFragment);
                currentItem = 0;
                setCurrentFragment(translateFragment);
                break;
            case R.id.navigation_history:
                HistoryFragment historyFragment;
                if (fragment == null || !(fragment instanceof HistoryFragment)) {
                    historyFragment = HistoryFragment.newInstance();
                } else {
                    historyFragment = (HistoryFragment) fragment;
                }
                mPresenter = new HistoryPresenter(Util.provideTranslateRepository(getApplicationContext()), historyFragment);
                currentItem = 1;
                setCurrentFragment(historyFragment);
                break;
            case R.id.navigation_favourite:
                FavouriteFragment favouriteFragment;
                if (fragment == null || !(fragment instanceof FavouriteFragment)) {
                    favouriteFragment = FavouriteFragment.newInstance();
                } else {
                    favouriteFragment = (FavouriteFragment) fragment;
                }
                mPresenter = new FavouritePresenter(Util.provideTranslateRepository(getApplicationContext()), favouriteFragment);
                currentItem = 2;
                setCurrentFragment(favouriteFragment);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (currentItem == 0) {
            super.onBackPressed();
        } else {
            navigation.setSelectedItemId(R.id.navigation_translate);
        }
    }

    public void setCurrentFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentItem > prevItem) {
            transaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left);
        } else {
            transaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
        }
        prevItem = currentItem;

        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}
