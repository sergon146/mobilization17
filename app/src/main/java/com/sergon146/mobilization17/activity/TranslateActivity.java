package com.sergon146.mobilization17.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.history.favourite.FavFragment;
import com.sergon146.mobilization17.history.favourite.FavPresenter;
import com.sergon146.mobilization17.translate.TrFragment;
import com.sergon146.mobilization17.translate.TrPresenter;
import com.sergon146.mobilization17.util.Util;

public class TranslateActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private int currentItem = 0;
    // TODO: 13.04.2017 Broadcast

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        initNavBar();
        initStartFragment();
    }

    private void initStartFragment() {
        navigation.setSelectedItemId(currentItem);
    }

    private void initNavBar() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(currentItem);
        navigation.setOnNavigationItemSelectedListener(item -> {
            setCurrentFragmentByItem(item);
            item.setChecked(true);
            return false;
        });
    }

    public void setCurrentFragmentByItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_translate:
                TrFragment trFragment = TrFragment.newInstance();
                new TrPresenter(Util.provideTasksRepository(getApplicationContext()), trFragment);
                setCurrentFragment(trFragment);
                currentItem = 0;
                break;
            case R.id.navigation_history:
                currentItem = 1;
                break;
            case R.id.navigation_favourite:
                FavFragment favFragment = FavFragment.newInstance();
                new FavPresenter(Util.provideTasksRepository(getApplicationContext()), favFragment);
                setCurrentFragment(favFragment);
                currentItem = 2;
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
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}
