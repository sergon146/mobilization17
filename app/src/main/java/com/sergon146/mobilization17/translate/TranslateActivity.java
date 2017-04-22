package com.sergon146.mobilization17.translate;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.history.favourite.FavouriteFragment;
import com.sergon146.mobilization17.history.favourite.FavouritePresenter;
import com.sergon146.mobilization17.util.Util;

public class TranslateActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private int currentItem = R.id.navigation_translate;

    //коментарии в коде идут вразрез концепции чистого кода описанного Р. Мартином :)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        setCurrentFragmentByItemId(currentItem);
        initNavBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentItem = navigation.getSelectedItemId();
        setCurrentFragmentByItemId(currentItem);
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
               new TranslatePresenter(Util.provideTasksRepository(getApplicationContext()), translateFragment);
                setCurrentFragment(translateFragment);
                break;
            case R.id.navigation_history:
//                FavouriteFragment favouriteFragment;
//                if (fragment == null || !(fragment instanceof FavouriteFragment)) {
//                    favouriteFragment = FavouriteFragment.newInstance();
//                } else {
//                    favouriteFragment = (FavouriteFragment) fragment;
//                }
//                new FavouritePresenter(Util.provideTasksRepository(getApplicationContext()), favouriteFragment);
//                setCurrentFragment(favouriteFragment);
                break;
            case R.id.navigation_favourite:
                FavouriteFragment favouriteFragment;
                if (fragment == null || !(fragment instanceof FavouriteFragment)) {
                    favouriteFragment = FavouriteFragment.newInstance();
                } else {
                    favouriteFragment = (FavouriteFragment) fragment;
                }
                new FavouritePresenter(Util.provideTasksRepository(getApplicationContext()), favouriteFragment);
                setCurrentFragment(favouriteFragment);
                break;
        }
        currentItem = itemId;
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
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}
