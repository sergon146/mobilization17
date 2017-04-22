package com.sergon146.mobilization17.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ViewUtil {

    public static void animateClick(View v, int andimId) {
        Animation rotate = AnimationUtils.loadAnimation(v.getContext(), andimId);
        v.startAnimation(rotate);
    }
}
