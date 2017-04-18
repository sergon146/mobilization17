package com.sergon146.mobilization17.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.presenter.TranslateActivityPresenter;
import com.sergon146.mobilization17.presenter.impl.TranslateActivityPresenterImpl;

import java.util.List;

public class TranslateActivity extends AppCompatActivity {
    private TranslateActivityPresenter presenter;
    private BottomNavigationView navigation;
    // TODO: 13.04.2017 Broadcast

    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        presenter = new TranslateActivityPresenterImpl(this);

        initStartFragment();

        initNavBar();
//        initPager();
    }

    private void initStartFragment() {

    }

    private void initNavBar() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            presenter.setCurrentFragmentByItem(item);
            item.setChecked(true);
            return false;
        });
    }

    public void setCurrentFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (presenter.getCurrentItem() == 0)  {
            super.onBackPressed();
        } else {
            navigation.setSelectedItemId(R.id.navigation_translate);
        }
    }
}
