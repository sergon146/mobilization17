package com.sergon146.mobilization17.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.sergon146.mobilization17.adapter.PageAdapter;

public class CustomViewPager extends ViewPager {
    private PageAdapter adapter;

    @Override
    public PageAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(PageAdapter adapter) {
        this.adapter = adapter;
    }

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }


}