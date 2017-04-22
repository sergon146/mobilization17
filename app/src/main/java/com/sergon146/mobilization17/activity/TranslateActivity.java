package com.sergon146.mobilization17.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.history.favourite.FavFragment;
import com.sergon146.mobilization17.history.favourite.FavPresenter;
import com.sergon146.mobilization17.translate.TrFragment;
import com.sergon146.mobilization17.translate.TrPresenter;
import com.sergon146.mobilization17.util.Util;

public class TranslateActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private int currentItem = R.id.navigation_translate;

    //коментарии в коде идут вразрез концепции чистого кода описанного Мартином :)

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
            return false;
        });
    }

    public void setCurrentFragmentByItemId(int itemId) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        switch (itemId) {
            case R.id.navigation_translate:
                TrFragment trFragment;
                if (fragment == null || !(fragment instanceof TrFragment)) {
                    trFragment = TrFragment.newInstance();
                } else {
                    trFragment = (TrFragment) fragment;
                }
               new TrPresenter(Util.provideTasksRepository(getApplicationContext()), trFragment);
                setCurrentFragment(trFragment);
                break;
            case R.id.navigation_history:
//                FavFragment favFragment;
//                if (fragment == null || !(fragment instanceof FavFragment)) {
//                    favFragment = FavFragment.newInstance();
//                } else {
//                    favFragment = (FavFragment) fragment;
//                }
//                new FavPresenter(Util.provideTasksRepository(getApplicationContext()), favFragment);
//                setCurrentFragment(favFragment);
                break;
            case R.id.navigation_favourite:
                FavFragment favFragment;
                if (fragment == null || !(fragment instanceof FavFragment)) {
                    favFragment = FavFragment.newInstance();
                } else {
                    favFragment = (FavFragment) fragment;
                }
                new FavPresenter(Util.provideTasksRepository(getApplicationContext()), favFragment);
                setCurrentFragment(favFragment);
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
