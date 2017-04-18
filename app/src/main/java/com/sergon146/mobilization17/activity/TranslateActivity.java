package com.sergon146.mobilization17.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.adapter.PageAdapter;
import com.sergon146.mobilization17.fragment.HistoryFragment;
import com.sergon146.mobilization17.presenter.TranslateActivityPresenter;
import com.sergon146.mobilization17.presenter.impl.TranslateActivityPresenterImpl;
import com.sergon146.mobilization17.view.CustomViewPager;

import java.util.List;

public class TranslateActivity extends AppCompatActivity {
    private TranslateActivityPresenter presenter;
    private ViewPager mViewPager;
    private BottomNavigationView navigation;
    // TODO: 13.04.2017 Broadcast

    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        presenter = new TranslateActivityPresenterImpl(this);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        initPager();
        initNavBar();
    }

    private void initPager() {
        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        if (mViewPager.getAdapter() == null) {
            PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());
            fragments = pageAdapter.getFragments();
            mViewPager.setAdapter(pageAdapter);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 1) {
                        ((HistoryFragment) fragments.get(1)).updateData();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        mViewPager.setCurrentItem(0);
    }

    private void initNavBar() {
        navigation.setOnNavigationItemSelectedListener(item -> {
            presenter.setCurrentFragmentByItem(item);
            item.setChecked(true);
            return false;
        });
    }
    public void setCurrentItem(int pos) {
        mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0)  {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(0);
            navigation.setSelectedItemId(R.id.navigation_translate);
        }
    }


}
